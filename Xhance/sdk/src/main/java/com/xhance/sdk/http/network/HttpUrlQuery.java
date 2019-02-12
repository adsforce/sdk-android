package com.xhance.sdk.http.network;

import android.text.TextUtils;

import com.xhance.sdk.utils.Constants;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUrlQuery {
    private StringBuffer mQuery = new StringBuffer();

    public HttpUrlQuery addParams(Map<String, String> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                if (!TextUtils.isEmpty(key) && map.get(key) != null) {
                    this.add(key, map.get(key));
                }
            }
        }

        return this;
    }

    public HttpUrlQuery addParams(HashMap<String, String> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                if (!TextUtils.isEmpty(key) && map.get(key) != null) {
                    this.add(key, map.get(key));
                }
            }
        }

        return this;
    }

    private HttpUrlQuery add(String key, String value) {
        try {
            if (mQuery.length() > 0) {
                mQuery.append('&');
            }

            if (TextUtils.isEmpty(value)) {
                value = "";
            }
            mQuery.append(URLEncoder.encode(key, Constants.ENCODE_UTF8)).append('=').
                    append(URLEncoder.encode(value, Constants.ENCODE_UTF8));
        } catch (Throwable e) {

        }

        return this;
    }

    public String appendToUrl(String url) {
        return url + (url.contains("?") ? "&" : "?") + toString();
    }

    @Override
    public String toString() {
        return mQuery.toString();
    }
}
