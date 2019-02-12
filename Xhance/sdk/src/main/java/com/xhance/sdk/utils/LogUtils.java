package com.xhance.sdk.utils;

import android.util.Log;

import com.xhance.sdk.BuildConfig;

public class LogUtils {
    public static final String TAG = "XhanceSdk_" + BuildConfig.VERSION_CODE;
    private static boolean mLogger = false;

    public static void enableLogger(boolean logger) {
        LogUtils.mLogger = logger;
    }

    public static void verbose(String message) {
        if (!mLogger) {
            return;
        }
        Log.v(TAG, message);
    }

    public static void debug(String message) {
        if (!mLogger) {
            return;
        }
        Log.d(TAG, message);
    }

    public static void info(String message) {
        if (!mLogger) {
            return;
        }
        Log.i(TAG, message);
    }

    public static void warn(String message, Throwable throwable) {
        if (!mLogger) {
            return;
        }
        Log.w(TAG, message, throwable);
    }

    public static void error(String message, Throwable throwable) {
        if (!mLogger) {
            return;
        }
        Log.e(TAG, message, throwable);
    }

}
