package com.xhance.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.xhance.sdk.config.XhanceSdkConfig;
import com.xhance.sdk.deeplink.XhanceDeepLinkCallback;
import com.xhance.sdk.deeplink.XhanceDeepLinkManager;
import com.xhance.sdk.event.log.XhanceLogEventManager;
import com.xhance.sdk.event.revenue.XhanceRevenueManager;
import com.xhance.sdk.event.session.XhanceSessionManager;
import com.xhance.sdk.install.XhanceInstallManager;
import com.xhance.sdk.utils.DeviceInfo;
import com.xhance.sdk.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XhanceSdk {
    private static Context mContext;
    private static XhanceSdkConfig mSdkConfig;
    private static boolean sSdkInited = false;
    private static boolean sCustomerEventEnable = false;

    private static XhanceInstallManager sInstallManager;
    private static XhanceSessionManager sSessionManager;
    private static XhanceRevenueManager sRevenueManager;
    private static XhanceLogEventManager sLogEventManager;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    public static XhanceSdkConfig getSdkConfig() {
        return mSdkConfig;
    }

    public static void initSdk(@NonNull Activity activity, @NonNull String devKey, @NonNull String publicKey,
                               @NonNull String trackUrl, @NonNull String channelId) {
        if (activity == null) {
            Log.w(LogUtils.TAG, "Xhance SDK init failed, activity can not be null");
            return;
        }
        initSdk(activity.getApplication(), devKey, publicKey, trackUrl, channelId);
    }

    public static void initSdk(@NonNull Application application, @NonNull String devKey, @NonNull String publicKey,
                               @NonNull String trackUrl, @NonNull String channelId) {
        if (application == null) {
            Log.w(LogUtils.TAG, "Xhance SDK init failed, application can not be null");
            return;
        }

        XhanceSdkConfig config = new XhanceSdkConfig(devKey, publicKey, trackUrl, channelId);
        if (!config.isValid()) {
            Log.w(LogUtils.TAG, "Xhance SDK init failed, config is not valid");
            return;
        }

        mContext = application.getApplicationContext();
        mSdkConfig = config;
        sSdkInited = true;

        sInstallManager = XhanceInstallManager.getInstance();
        sInstallManager.init(application.getApplicationContext());

        sSessionManager = XhanceSessionManager.getInstance();
        sSessionManager.init(application);
    }

    public static void getDeepLink(@NonNull Context context, @NonNull XhanceDeepLinkCallback callback) {
        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "XhanceSdk has not inited, firstly call initSdk please");
            return;
        }

        if (callback == null) {
            Log.w(LogUtils.TAG, "getDeepLink is called, callback can not be null");
            return;
        }

        XhanceDeepLinkManager.getInstance().fetchDeepLink(context, callback);
    }

    public static void thirdPayWithProductPrice(@NonNull String price, @NonNull String currency,
                                                String productId, String productType) {
        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "XhanceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(price)) {
            Log.w(LogUtils.TAG, "thirdPayWithProductPrice is called, price can not be empty");
            return;
        }
        if (TextUtils.isEmpty(currency)) {
            Log.w(LogUtils.TAG, "thirdPayWithProductPrice is called, currency can not be empty");
            return;
        }

        if (sRevenueManager == null) {
            sRevenueManager = XhanceRevenueManager.getInstance();
            sRevenueManager.init(mContext);
        }
        sRevenueManager.logRevenue(price, currency, productId, productType);
    }

    public static void googlePayWithProductPrice(@NonNull String price, @NonNull String currency,
                                                 @NonNull String publicKey, @NonNull String dataSignature,
                                                 @NonNull String purchaseData, Map<String, String> params) {
        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "XhanceSdk has not inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(price)) {
            Log.w(LogUtils.TAG, "googlePayWithProductPrice is called, price can not be empty");
            return;
        }
        if (TextUtils.isEmpty(currency)) {
            Log.w(LogUtils.TAG, "googlePayWithProductPrice is called, currency can not be empty");
            return;
        }
        if (TextUtils.isEmpty(publicKey)) {
            Log.w(LogUtils.TAG, "googlePayWithProductPrice is called, publicKey can not be empty");
            return;
        }
        if (TextUtils.isEmpty(dataSignature)) {
            Log.w(LogUtils.TAG, "googlePayWithProductPrice is called, dataSignature can not be empty");
            return;
        }
        if (TextUtils.isEmpty(purchaseData)) {
            Log.w(LogUtils.TAG, "googlePayWithProductPrice is called, purchaseData can not be empty");
            return;
        }

        if (sRevenueManager == null) {
            sRevenueManager = XhanceRevenueManager.getInstance();
            sRevenueManager.init(mContext);
        }
        sRevenueManager.logRevenueVerify(price, currency, publicKey, dataSignature, purchaseData, params);
    }

    public static void customerEventWithValue(String key, String value) {
        if (!sCustomerEventEnable) {
            Log.w(LogUtils.TAG, "XhanceSdk customer event is disable, call enableCustomerEvent first");
            return;
        }

        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "XhanceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(key)) {
            Log.w(LogUtils.TAG, "customerEventWithValue is called, key can not be empty ");
            return;
        }

        if (sLogEventManager == null) {
            sLogEventManager = XhanceLogEventManager.getInstance();
            sLogEventManager.init(mContext);
        }
        sLogEventManager.logEventByValue(key, value);
    }

    public static void customerEventWithMap(String key, HashMap<String, String> map) {
        if (!sCustomerEventEnable) {
            Log.w(LogUtils.TAG, "XhanceSdk customer event is disable, call enableCustomerEvent first");
            return;
        }

        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "XhanceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(key)) {
            Log.w(LogUtils.TAG, "customerEventWithMap is called, key can not be empty ");
            return;
        }

        if (sLogEventManager == null) {
            sLogEventManager = XhanceLogEventManager.getInstance();
            sLogEventManager.init(mContext);
        }
        sLogEventManager.logEventByMap(key, map);
    }

    public static void customerEventWithList(String key, ArrayList<String> list) {
        if (!sCustomerEventEnable) {
            Log.w(LogUtils.TAG, "XhanceSdk customer event is disable, call enableCustomerEvent first");
            return;
        }

        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "XhanceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(key)) {
            Log.w(LogUtils.TAG, "customerEventWithList is called, key can not be empty ");
            return;
        }

        if (sLogEventManager == null) {
            sLogEventManager = XhanceLogEventManager.getInstance();
            sLogEventManager.init(mContext);
        }
        sLogEventManager.logEventByList(key, list);
    }

    public static void enableCustomerEvent(boolean enable) {
        sCustomerEventEnable = enable;
    }

    public static void setTestMode(boolean testMode) {
        XhanceSdkConfig.setTestMode(testMode);
    }

    public static void enableLogger(boolean logger) {
        XhanceSdkConfig.enableLogger(logger);
    }

    public static void setAndroidId(@NonNull Context context, String androidId) {
        DeviceInfo.getInstance().setAndroidId(context, androidId);
    }

}
