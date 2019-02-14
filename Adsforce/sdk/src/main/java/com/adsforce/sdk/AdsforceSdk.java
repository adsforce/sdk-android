package com.adsforce.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.adsforce.sdk.config.AdsforceSdkConfig;
import com.adsforce.sdk.deeplink.AdsforceDeepLinkCallback;
import com.adsforce.sdk.deeplink.AdsforceDeepLinkManager;
import com.adsforce.sdk.event.log.AdsforceLogEventManager;
import com.adsforce.sdk.event.revenue.AdsforceRevenueManager;
import com.adsforce.sdk.event.session.AdsforceSessionManager;
import com.adsforce.sdk.http.network.HttpDnsManager;
import com.adsforce.sdk.install.AdsforceInstallManager;
import com.adsforce.sdk.utils.DeviceInfo;
import com.adsforce.sdk.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by t.wang on 2018/4/12.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */
public class AdsforceSdk {
    private static Context mContext;
    private static AdsforceSdkConfig mSdkConfig;
    private static boolean sSdkInited = false;
    private static boolean sDnsModeEnable = false;
    private static boolean sCustomerEventEnable = false;

    private static AdsforceInstallManager sInstallManager;
    private static AdsforceSessionManager sSessionManager;
    private static AdsforceRevenueManager sRevenueManager;
    private static AdsforceLogEventManager sLogEventManager;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    public static AdsforceSdkConfig getSdkConfig() {
        return mSdkConfig;
    }

    public static void initSdk(@NonNull Activity activity, @NonNull String devKey, @NonNull String publicKey,
                               @NonNull String trackUrl, @NonNull String channelId) {
        if (activity == null) {
            Log.w(LogUtils.TAG, "Adsforce SDK init failed, activity can not be null");
            return;
        }
        initSdk(activity.getApplication(), devKey, publicKey, trackUrl, channelId);
    }

    public static void initSdk(@NonNull Application application, @NonNull String devKey, @NonNull String publicKey,
                               @NonNull String trackUrl, @NonNull String channelId) {
        if (application == null) {
            Log.w(LogUtils.TAG, "Adsforce SDK init failed, application can not be null");
            return;
        }

        AdsforceSdkConfig config = new AdsforceSdkConfig(devKey, publicKey, trackUrl, channelId);
        if (!config.isValid()) {
            Log.w(LogUtils.TAG, "Adsforce SDK init failed, config is not valid");
            return;
        }

        mContext = application.getApplicationContext();
        mSdkConfig = config;
        sSdkInited = true;

        sInstallManager = AdsforceInstallManager.getInstance();
        sInstallManager.init(application.getApplicationContext());

        sSessionManager = AdsforceSessionManager.getInstance();
        sSessionManager.init(application);
    }

    public static void getDeepLink(@NonNull Context context, @NonNull AdsforceDeepLinkCallback callback) {
        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "AdsforceSdk has not inited, firstly call initSdk please");
            return;
        }

        if (callback == null) {
            Log.w(LogUtils.TAG, "getDeepLink is called, callback can not be null");
            return;
        }

        AdsforceDeepLinkManager.getInstance().fetchDeepLink(context, callback);
    }

    public static void thirdPayWithProductPrice(@NonNull String price, @NonNull String currency,
                                                String productId, String productType) {
        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "AdsforceSdk has not be inited, firstly call initSdk please");
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
            sRevenueManager = AdsforceRevenueManager.getInstance();
            sRevenueManager.init(mContext);
        }
        sRevenueManager.logRevenue(price, currency, productId, productType);
    }

    public static void googlePayWithProductPrice(@NonNull String price, @NonNull String currency,
                                                 @NonNull String publicKey, @NonNull String dataSignature,
                                                 @NonNull String purchaseData, Map<String, String> params) {
        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "AdsforceSdk has not inited, firstly call initSdk please");
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
            sRevenueManager = AdsforceRevenueManager.getInstance();
            sRevenueManager.init(mContext);
        }
        sRevenueManager.logRevenueVerify(price, currency, publicKey, dataSignature, purchaseData, params);
    }

    public static void customerEventWithValue(String key, String value) {
        if (!sCustomerEventEnable) {
            Log.w(LogUtils.TAG, "AdsforceSdk customer event is disable, call enableCustomerEvent first");
            return;
        }

        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "AdsforceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(key)) {
            Log.w(LogUtils.TAG, "customerEventWithValue is called, key can not be empty ");
            return;
        }

        if (sLogEventManager == null) {
            sLogEventManager = AdsforceLogEventManager.getInstance();
            sLogEventManager.init(mContext);
        }
        sLogEventManager.logEventByValue(key, value);
    }

    public static void customerEventWithMap(String key, HashMap<String, String> map) {
        if (!sCustomerEventEnable) {
            Log.w(LogUtils.TAG, "AdsforceSdk customer event is disable, call enableCustomerEvent first");
            return;
        }

        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "AdsforceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(key)) {
            Log.w(LogUtils.TAG, "customerEventWithMap is called, key can not be empty ");
            return;
        }

        if (sLogEventManager == null) {
            sLogEventManager = AdsforceLogEventManager.getInstance();
            sLogEventManager.init(mContext);
        }
        sLogEventManager.logEventByMap(key, map);
    }

    public static void customerEventWithList(String key, ArrayList<String> list) {
        if (!sCustomerEventEnable) {
            Log.w(LogUtils.TAG, "AdsforceSdk customer event is disable, call enableCustomerEvent first");
            return;
        }

        if (!sSdkInited) {
            Log.w(LogUtils.TAG, "AdsforceSdk has not be inited, firstly call initSdk please");
            return;
        }

        if (TextUtils.isEmpty(key)) {
            Log.w(LogUtils.TAG, "customerEventWithList is called, key can not be empty ");
            return;
        }

        if (sLogEventManager == null) {
            sLogEventManager = AdsforceLogEventManager.getInstance();
            sLogEventManager.init(mContext);
        }
        sLogEventManager.logEventByList(key, list);
    }

    public static void enableCustomerEvent(boolean enable) {
        sCustomerEventEnable = enable;
    }

    public static void setTestMode(boolean testMode) {
        AdsforceSdkConfig.setTestMode(testMode);
    }

    public static void enableLogger(boolean logger) {
        AdsforceSdkConfig.enableLogger(logger);
    }

    public static void setAndroidId(@NonNull Context context, String androidId) {
        DeviceInfo.getInstance().setAndroidId(context, androidId);
    }

    public static void enableDnsMode(boolean enable) {
        sDnsModeEnable = enable;
    }

    public static boolean isDnsModeEnable() {
        return sDnsModeEnable;
    }

    public static void addDnsMappingServers(String domain, ArrayList<String> dnsServerList) {
        if (!sDnsModeEnable) {
            Log.w(LogUtils.TAG, "AdsforceSdk dns mode is disable, call enableDnsMode first");
            return;
        }
        if (TextUtils.isEmpty(domain)) {
            Log.w(LogUtils.TAG, "AdsforceSdk domain is invalid, check first");
            sDnsModeEnable = false;
            return;
        }
        if (dnsServerList == null || dnsServerList.isEmpty()) {
            Log.w(LogUtils.TAG, "AdsforceSdk dnsServerList is invalid, check first");
            sDnsModeEnable = false;
            return;
        }
        HttpDnsManager.addDnsMappingServers(domain, dnsServerList);
    }
}
