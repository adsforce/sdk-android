package com.adsforce.sdk.http.network;

import android.text.TextUtils;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class HttpUtils {
    public static String httpPost(final String url) throws Exception {
        return httpPost(url, null);
    }

    public static String httpPost(final String url, final String requestBody) throws Exception {
        return httpPost(url, requestBody, null);
    }

    public static String httpPost(final String url, final String requestBody, final String userAgent) throws Exception {
        return httpPost(url, requestBody, userAgent, null);
    }

    public static String httpPost(final String url, final String requestBody, final String userAgent, final String enctyType) throws Exception {
        return httpPost(url, requestBody, userAgent, enctyType, null);
    }

    public static String httpPost(final String url, final String requestBody, final String userAgent, final String enctyType, final String hostName) throws Exception {
        return httpSend("post", url, requestBody, userAgent, enctyType, hostName);
    }

    public static String httpSend(String type, final String url, final String requestBody,
                                  final String userAgent, final String enctyType, final String hostName) throws Exception {
        HttpClient client = HttpClient.builder().setUrl(url);
        if (type.equals("post")) {
            client.setRequestMethod(HttpClient.METHOD_POST);
        }
        if (!TextUtils.isEmpty(requestBody)) {
            client.setRequestBody(requestBody);
        }
        if (!TextUtils.isEmpty(userAgent)) {
            client.setUserAgent(userAgent);
        }
        if (!TextUtils.isEmpty(enctyType)) {
            client.setEnctyType(enctyType);
        }
        if (!TextUtils.isEmpty(hostName)) {
            client.setHostName(hostName);
        }

        String body = client.request().getBody();
        return body;
    }

}
