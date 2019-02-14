package com.adsforce.sdk.http.network;

import android.text.TextUtils;

import com.adsforce.sdk.utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */
public class HttpClient {
    public static String METHOD_GET = "GET";
    public static String METHOD_HEAD = "HEAD";
    public static String METHOD_POST = "POST";

    private URL mUrl;
    private int mConnectTimeout = 10 * 1000;
    private int mReadTimeout = 15 * 1000;
    private boolean mUseCaches = false;
    private String mRequestMethod = METHOD_GET;
    private String mRequestBody = null;
    private String mUserAgent = null;
    private String mHostName = null;
    private String mEnctyType = null;
    private Map<String, String> mHeaderMap = new ConcurrentHashMap<>();
    private HttpURLConnection mConn;

    public static HttpClient builder() {
        return new HttpClient();
    }

    private HttpClient() {
    }

    public HttpClient setUrl(String url) throws MalformedURLException {
        this.mUrl = new URL(url);
        return this;
    }

    public HttpClient setConnectTimeout(int timeout) {
        mConnectTimeout = timeout;
        return this;
    }

    public HttpClient setReadTimeout(int timeout) {
        mReadTimeout = timeout;
        return this;
    }

    public HttpClient setUseCaches(boolean use) {
        mUseCaches = use;
        return this;
    }

    public HttpClient setRequestMethod(String method) {
        mRequestMethod = method;
        return this;
    }

    public HttpClient setRequestHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            mHeaderMap.put(key, value);
        }
        return this;
    }

    public HttpClient setRequestBody(String body) {
        mRequestBody = body;
        return this;
    }

    public HttpClient setUserAgent(String userAgent) {
        this.mUserAgent = userAgent;
        return this;
    }

    public HttpClient setHostName(String hostName) {
        this.mHostName = hostName;
        return this;
    }

    public HttpClient setEnctyType(String enctyType) {
        this.mEnctyType = enctyType;
        return this;
    }

    public HttpResponse request() throws Exception {
        if (mUrl == null)
            throw new IOException("URL is empty");

        makeConnection();

        return new HttpResponse(mConn);
    }

    private void makeConnection() throws Exception {
        mConn = (HttpURLConnection) mUrl.openConnection();
        mConn.setConnectTimeout(mConnectTimeout);
        mConn.setReadTimeout(mReadTimeout);
        mConn.setUseCaches(mUseCaches);
        mConn.setRequestMethod(mRequestMethod);
        mConn.setInstanceFollowRedirects(true);

        if (mUserAgent != null) {
            mConn.setRequestProperty("User-Agent", mUserAgent);
        }

        if (mHostName != null) {
            mConn.setRequestProperty("Host", mHostName);
        }

        if (mHeaderMap != null && !mHeaderMap.isEmpty()) {
            for (String key : mHeaderMap.keySet()) {
                mConn.setRequestProperty(key, mHeaderMap.get(key));
            }
        }

        if (METHOD_GET.equals(mRequestMethod) || METHOD_HEAD.equals(mRequestMethod)) {
            mConn.setDoOutput(false);
        } else if (METHOD_POST.equals(mRequestMethod)) {
            mConn.setDoOutput(true);
        }

        if (mRequestBody != null) {
            if (mEnctyType != null) {
                mConn.setRequestProperty("Content-type", mEnctyType);
            } else {
                mConn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            }

            OutputStream os = mConn.getOutputStream();
            byte[] bytes = mRequestBody.getBytes(Constants.ENCODE_UTF8);
            os.write(bytes);
            os.close();
        }
    }
}
