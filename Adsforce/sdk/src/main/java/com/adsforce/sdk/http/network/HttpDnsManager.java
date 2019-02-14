package com.adsforce.sdk.http.network;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.adsforce.sdk.http.callback.Callback;
import com.adsforce.sdk.manager.WorkThreadManager;
import com.adsforce.sdk.utils.LogUtils;
import com.adsforce.sdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by t.wang on 2018/11/26.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */
public class HttpDnsManager {
    private static final Pattern IP_REG = Pattern.compile("(\\d*)\\.(\\d*)\\.(\\d*)\\.(\\d*)");
    private static final long DNS_IP_CACHE_MILLS = 10 * DateUtils.MINUTE_IN_MILLIS;

    private static String sDnsDomain;
    private static long sLastCallDnsMills;
    private static ArrayList<String> sDnsServerList = new ArrayList<>();
    private static HashMap<String, HashSet<String>> sDnsMap = new HashMap<>();

    public static HashMap<String, HashSet<String>> getDnsMap() {
        return sDnsMap;
    }

    public static String getUrlDomain() {
        return sDnsDomain;
    }

    public static void addDnsMappingServers(String domain, ArrayList<String> dnsServerList) {
        sDnsDomain = domain;
        sDnsServerList = Utils.getShuffleList(dnsServerList);
    }

    public static void callForClientDnsIp(final Callback callback) {
        if (sDnsMap.containsKey(sDnsDomain) && System.currentTimeMillis() - sLastCallDnsMills <= DNS_IP_CACHE_MILLS) {
            callback.onSuccess("");
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    callForClientDnsIp(sDnsDomain, 0, callback);
                }
            };
            if (sDnsMap != null && sDnsMap.containsKey(sDnsDomain)) {
                sDnsMap.remove(sDnsDomain);
            }
            WorkThreadManager.runInHttpThreadPool(runnable);
        }
    }

    private static void callForClientDnsIp(String domain, int currentIndex, final Callback callback) {
        boolean ipFound = false;
        String dnsUrl = sDnsServerList.get(currentIndex);
        LogUtils.debug("HttpDnsManager callForClientDnsIp dnsUrl is " + dnsUrl);
        try {
            String body = HttpClient.builder().setUrl(dnsUrl).request().getBody();
            if (!TextUtils.isEmpty(body)) {
                JSONObject json = new JSONObject(body);
                if (json.has("answer")) {
                    JSONArray array = json.getJSONArray("answer");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.optJSONObject(i);
                            String type = item.optString("type");
                            if (!"A".equals(type)) {
                                continue;
                            }

                            String ip = item.optString("rdata");
                            Matcher matcher = IP_REG.matcher(ip);
                            if (matcher.matches()) {
                                addToDnsMap(domain, ip);
                                ipFound = true;
                            }
                        }
                    }
                }
            }

            if (!ipFound) {
                callNextDnsServer(domain, currentIndex, callback);
            } else {
                sLastCallDnsMills = System.currentTimeMillis();
                callback.onSuccess("");
            }
        } catch (Throwable e) {
            LogUtils.warn("HttpDnsManager callForClientDnsIp error: ", e);

            if (!ipFound) {
                callNextDnsServer(domain, currentIndex, callback);
            } else {
                sLastCallDnsMills = System.currentTimeMillis();
                callback.onSuccess("");
            }
        }
    }

    private static void callNextDnsServer(String domain, int index, Callback callback) {
        index = index + 1;
        if (index < sDnsServerList.size()) {
            callForClientDnsIp(domain, index, callback);
        } else {
            callback.onFailed(new Exception("HttpDnsManager callForClientDnsIp failed "));
        }
    }

    private static void addToDnsMap(String domain, String ip) {
        if (sDnsMap == null) {
            sDnsMap = new HashMap<>();
        }

        LogUtils.debug("HttpDnsManager addToDnsMap " + domain + " mapping " + ip);

        HashSet<String> ipSet = new HashSet();
        if (sDnsMap.containsKey(domain)) {
            ipSet = sDnsMap.get(domain);
        } else {
            sDnsMap.put(domain, ipSet);
        }
        ipSet.add(ip);
    }

}
