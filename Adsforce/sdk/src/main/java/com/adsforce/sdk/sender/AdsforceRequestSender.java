package com.adsforce.sdk.sender;

import android.content.Context;
import android.text.TextUtils;

import com.adsforce.sdk.AdsforceSdk;
import com.adsforce.sdk.config.AdsforceSdkConfig;
import com.adsforce.sdk.http.callback.Callback;
import com.adsforce.sdk.http.network.HttpDnsManager;
import com.adsforce.sdk.http.network.HttpUrlQuery;
import com.adsforce.sdk.http.network.HttpUtils;
import com.adsforce.sdk.security.AESUtils;
import com.adsforce.sdk.security.Md5Utils;
import com.adsforce.sdk.security.RSAUtils;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.DeviceInfo;
import com.adsforce.sdk.utils.LogUtils;
import com.adsforce.sdk.utils.Utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceRequestSender {
    private static ArrayList<String> sClientDnsIpList;

    public static void sendDataToClient(String action, String request, long timestamp, String uuid,
                                        final Callback<String> callback) {
        LogUtils.info("HttpRequestSender sendDataToClient is called, request is \n" + request);

        String requestUrl = "";
        try {
            AdsforceSdkConfig config = AdsforceSdk.getSdkConfig();
            if (config == null) {
                callback.onFailed(new Exception("sendDataToClient config is null"));
                return;
            }

            String url = config.getTrackerUrl();
            if (TextUtils.isEmpty(url) || !Utils.isUrl(config.getTrackerUrl())) {
                callback.onFailed(new Exception("sendDataToClient url is invalid"));
                return;
            }

            if (TextUtils.isEmpty(request)) {
                callback.onFailed(new Exception("sendDataToClient send_params is empty"));
                return;
            }

            String aesKey = AESUtils.getRandomString(16);
            String key = RSAUtils.encryptData(aesKey, config.getPublicKey());
            String params = AESUtils.encryptAES(request, aesKey);
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(params)) {
                callback.onFailed(new Exception("sendDataToClient failed, k|d is empty"));
                return;
            }

            Context context = AdsforceSdk.getContext();
            String packageName = DeviceInfo.getInstance().getPackageName(context);
            StringBuffer sb = new StringBuffer(url);
            if (!url.endsWith("/")) {
                sb.append("/");
            }
            sb.append(action);
            sb.append("/");
            sb.append(Md5Utils.textOfMd5(packageName));
            sb.append("?pf=android&k=");
            sb.append(key);
            sb.append("&d=");
            sb.append(params);
            sb.append("&cts=");
            sb.append(timestamp);
            sb.append("&ahs=");
            sb.append(Md5Utils.textOfMd5(packageName));
            sb.append("&uuid=");
            sb.append(uuid);
            requestUrl = sb.toString();
            LogUtils.info("HttpRequestSender sendDataToClient url is \n" + requestUrl);

            String body = HttpUtils.httpPost(requestUrl);
            LogUtils.info("HttpRequestSender sendDataToClient body is  \n" + body);
            if (TextUtils.isEmpty(body)) {
                callback.onFailed(new Exception("sendDataToClient request failed"));
            } else {
                callback.onSuccess(body);
            }
        } catch (final UnknownHostException e) {
            LogUtils.warn("sendDataToClient has UnknownHostException ", null);
            retrySendByHttpDns(requestUrl, callback, e);
        } catch (Throwable throwable) {
            callback.onFailed(new Exception("sendDataToClient has error: " + throwable.getMessage()));
        }
    }

    private static void retrySendByHttpDns(final String requestUrl, final Callback callback, final Exception e) {
        if (AdsforceSdk.isDnsModeEnable() && !TextUtils.isEmpty(requestUrl)) {
            final String originalUrl = requestUrl;
            HttpDnsManager.callForClientDnsIp(new Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    try {
                        String urlDomain = HttpDnsManager.getUrlDomain();
                        if (!TextUtils.isEmpty(urlDomain)) {
                            sClientDnsIpList = Utils.getShuffleList(HttpDnsManager.getDnsMap().get(urlDomain));
                            if (sClientDnsIpList == null || sClientDnsIpList.isEmpty()) {
                                callback.onFailed(new Exception("sendDataToClient has error: " + e.getMessage()));
                                return;
                            }

                            sendToClientByDnsIp(originalUrl, urlDomain, 0, callback);
                        } else {
                            callback.onFailed(new Exception("sendDataToClient has error: " + e.getMessage()));
                        }
                    } catch (Throwable throwable) {
                        callback.onFailed(new Exception("sendDataToClient has error: " + throwable.getMessage()));
                    }
                }

                @Override
                public void onFailed(Exception ex) {
                    callback.onFailed(new Exception("sendDataToClient has error: " + e.getMessage()));
                }
            });
        } else {
            callback.onFailed(new Exception("sendDataToClient has error: " + e.getMessage()));
        }
    }

    private static void sendToClientByDnsIp(String originalUrl, String urlDomain, int index, Callback<String> callback) {
        try {
            String newUrl = originalUrl.replace(urlDomain, sClientDnsIpList.get(index));
            newUrl = newUrl.replace("https", "http");
            LogUtils.info("HttpRequestSender sendToClientByDnsIp url is \n" + newUrl);

            String body = HttpUtils.httpPost(newUrl, null, null, null, urlDomain);
            LogUtils.info("HttpRequestSender sendToClientByDnsIp body is  \n" + body);

            if (TextUtils.isEmpty(body)) {
                index = index + 1;
                if (index < Math.min(3, sClientDnsIpList.size())) {
                    sendToClientByDnsIp(originalUrl, urlDomain, index, callback);
                } else {
                    callback.onFailed(new Exception("sendDataToClient request failed"));
                }
            } else {
                callback.onSuccess(body);
            }
        } catch (Throwable throwable) {
            index = index + 1;
            if (index < Math.min(3, sClientDnsIpList.size())) {
                sendToClientByDnsIp(originalUrl, urlDomain, index, callback);
            } else {
                callback.onFailed(new Exception("sendDataToClient has error: " + throwable.getMessage()));
            }
        }
    }

    public static void sendDataToServer(String action, String request, long timestamp, String uuid,
                                        Callback<String> callback) {
        LogUtils.info("HttpRequestSender sendDataToServer is called, request is \n" + request);
        try {
            if (TextUtils.isEmpty(request)) {
                callback.onFailed(new Exception("sendDataToServer send_params is empty"));
                return;
            }

            Context context = AdsforceSdk.getContext();
            DeviceInfo deviceInfo = DeviceInfo.getInstance();
            String dvhs = "aid=" + deviceInfo.getAndroidId(context) + "&gaid=" + deviceInfo.getGaid(context)
                    + "&oid=" + deviceInfo.getOpenId(context);
            HashMap<String, String> params = new HashMap<>();
            params.put("cts", timestamp + "");
            params.put("ahs", Md5Utils.textOfMd5(deviceInfo.getPackageName(context)));
            params.put("dvhs", Md5Utils.textOfMd5(dvhs));
            params.put("dths", Md5Utils.textOfMd5(request));
            params.put("pf", "android");
            params.put("uuid", uuid);

            String url = getServerApi(action) + Md5Utils.textOfMd5(deviceInfo.getPackageName(context));
            url = new HttpUrlQuery().addParams(params).appendToUrl(url);
            LogUtils.info("HttpRequestSender sendDataToServer url is \n" + url);

            String body = HttpUtils.httpPost(url);
            LogUtils.info("HttpRequestSender sendDataToServer body is \n" + body);
            if (TextUtils.isEmpty(body)) {
                callback.onFailed(new Exception("sendDataToServer request failed"));
            } else {
                callback.onSuccess(body);
            }
        } catch (Throwable throwable) {
            callback.onFailed(new Exception("sendDataToServer has error: " + throwable.getMessage()));
        }
    }

    private static String getServerApi(String action) {
        String domain = Constants.SERVER_URL;
        return domain + action + "/";
    }
}
