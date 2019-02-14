package com.adsforce.sdk.config;

import android.text.TextUtils;
import android.util.Log;

import com.adsforce.sdk.utils.LogUtils;

/**
 * Created by t.wang on 2018/4/12.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceSdkConfig {
    private static boolean sTestMode;
    private String mDevKey;
    private String mPublicKey;
    private String mTrackerUrl;
    private String mChannelId;

    public static boolean isTestMode() {
        return sTestMode;
    }

    public static void setTestMode(boolean testMode) {
        sTestMode = testMode;
    }

    public static void enableLogger(boolean logger) {
        LogUtils.enableLogger(logger);
    }

    public AdsforceSdkConfig(String devKey, String publicKey, String trackerUrl, String channelId) {
        mDevKey = devKey;
        mPublicKey = publicKey;
        mTrackerUrl = trackerUrl;
        mChannelId = channelId;
    }

    public String getDevKey() {
        return mDevKey;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public String getTrackerUrl() {
        return mTrackerUrl;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public boolean isValid() {
        if (TextUtils.isEmpty(mDevKey)) {
            Log.w(LogUtils.TAG, "Inited devkey cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(mPublicKey)) {
            Log.w(LogUtils.TAG, "Inited publicKey cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(mTrackerUrl)) {
            Log.w(LogUtils.TAG, "Inited trackUrl cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(mChannelId)) {
            Log.w(LogUtils.TAG, "Inited channelId cannot be empty");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return " mDevKey is " + mDevKey + "\n"
                + " mPublicKey is " + mPublicKey + "\n"
                + " mTrackerUrl is " + mTrackerUrl + "\n"
                + " mChannelId is " + mChannelId + "\n";
    }

}
