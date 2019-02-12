package com.xhance.sdk.http.network;

import android.text.TextUtils;

public class HttpUtils {
    public static String httpGet(final String url, final String userAgent) {
        return httpSend("get", url, null, userAgent, null);
    }

    public static String httpPost(final String url, final String requestBody, final String userAgent) {
        return httpSend("post", url, requestBody, userAgent, null);
    }

    public static String httpPost(final String url, final String requestBody,
                                  final String userAgent, final String enctyType) {
        return httpSend("post", url, requestBody, userAgent, enctyType);
    }

    public static String httpSend(String type, final String url, final String requestBody,
                                  final String userAgent, final String enctyType) {
        String body = "";
        try {
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
            body = client.request().getBody();
        } catch (Throwable e) {

        }
        return body;
    }

}
