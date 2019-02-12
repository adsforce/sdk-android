package com.xhance.sdk.sender;

import android.content.Context;
import android.text.TextUtils;

import com.xhance.sdk.XhanceSdk;
import com.xhance.sdk.config.XhanceSdkConfig;
import com.xhance.sdk.http.callback.Callback;
import com.xhance.sdk.http.network.HttpUrlQuery;
import com.xhance.sdk.http.network.HttpUtils;
import com.xhance.sdk.security.AESUtils;
import com.xhance.sdk.security.Md5Utils;
import com.xhance.sdk.security.RSAUtils;
import com.xhance.sdk.utils.Constants;
import com.xhance.sdk.utils.DeviceInfo;
import com.xhance.sdk.utils.LogUtils;
import com.xhance.sdk.utils.Utils;

import java.util.HashMap;

public class XhanceRequestSender {

    public static void sendDataToClient(String action, String request, long timestamp, String uuid,
                                        Callback<String> callback) {
        LogUtils.info("HttpRequestSender sendDataToClient is called, request is \n" + request);
        try {
            XhanceSdkConfig config = XhanceSdk.getSdkConfig();
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

            Context context = XhanceSdk.getContext();
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
            LogUtils.info("HttpRequestSender sendDataToClient url is \n" + sb.toString());

            String body = HttpUtils.httpPost(sb.toString(), null, null);
            LogUtils.info("HttpRequestSender sendDataToClient body is  \n" + body);
            if (TextUtils.isEmpty(body)) {
                callback.onFailed(new Exception("sendDataToClient request failed"));
            } else {
                callback.onSuccess(body);
            }
        } catch (Throwable throwable) {
            callback.onFailed(new Exception("sendDataToClient has error: " + throwable.getMessage()));
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

            Context context = XhanceSdk.getContext();
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

            String body = HttpUtils.httpPost(url, null, null);
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
        String domain = XhanceSdkConfig.isTestMode() ? Constants.SERVER_URL_TEST : Constants.SERVER_URL;
        return domain + action + "/";
    }
}
