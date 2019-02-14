package com.adsforce.sdk.deeplink;

import android.content.Context;
import android.text.TextUtils;

import com.adsforce.sdk.manager.SharedPreferencesManager;
import com.adsforce.sdk.manager.WorkThreadManager;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.LogUtils;

import org.json.JSONObject;

/**
 * Created by t.wang on 2018/7/18.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceDeepLinkManager {
    private static AdsforceDeepLinkManager sInstance;
    private SharedPreferencesManager mSpManager;
    private Context mContext;

    public static AdsforceDeepLinkManager getInstance() {
        if (sInstance == null) {
            synchronized (AdsforceDeepLinkManager.class) {
                if (sInstance == null) {
                    sInstance = new AdsforceDeepLinkManager();
                }
            }
        }

        return sInstance;
    }

    public void init(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (mSpManager == null) {
            mSpManager = new SharedPreferencesManager(Constants.SP_DEEPLINK_FILE_NAME);
        }
    }

    public void parseDeepLink(Context context, JSONObject jsonObject) {
        try {
            init(context);

            String deepLinkUri = jsonObject.optString("dlink_url");
            String deepLinkArgs = jsonObject.optString("dlink_args");
            mSpManager.putString(mContext, Constants.KEY_DEEPLINK_URI, deepLinkUri);
            mSpManager.putString(mContext, Constants.KEY_DEEPLINK_ARGS, deepLinkArgs);
            mSpManager.putInt(mContext, Constants.KEY_DEEPLINK_STATUS, Constants.DEEPLINK_STATE_RETURNED);
        } catch (Throwable throwable) {

        } finally {
            recoveryMemoryIfNeccesary();
        }
    }

    public void fetchDeepLink(final Context context, final AdsforceDeepLinkCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    init(context);

                    if (callback == null) {
                        LogUtils.warn("fetchDeepLink callback is null", null);
                        return;
                    }

                    int deepLinkStatus = mSpManager.getInt(mContext, Constants.KEY_DEEPLINK_STATUS);
                    if (deepLinkStatus == Constants.DEEPLINK_STATE_FETCHED) {
                        callback.onFetchDeepLink(null);
                        return;
                    }

                    long sleepMills = 0;
                    while (deepLinkStatus == Constants.DEEPLINK_STATE_WAITING && sleepMills < 20 * 1000) {
                        Thread.sleep(500);
                        deepLinkStatus = mSpManager.getInt(mContext, Constants.KEY_DEEPLINK_STATUS);
                        sleepMills += 500;
                    }

                    if (deepLinkStatus == Constants.DEEPLINK_STATE_RETURNED) {
                        String deepLinkUri = mSpManager.getString(mContext, Constants.KEY_DEEPLINK_URI);
                        String deepLinkArgs = mSpManager.getString(mContext, Constants.KEY_DEEPLINK_ARGS);
                        if (TextUtils.isEmpty(deepLinkUri)) {
                            callback.onFetchDeepLink(null);
                        } else {
                            callback.onFetchDeepLink(new AdsforceDeepLink(deepLinkUri, deepLinkArgs));
                        }
                    } else {
                        callback.onFetchDeepLink(null);
                    }
                } catch (Throwable throwable) {
                    callback.onFetchDeepLink(null);
                } finally {
                    mSpManager.putInt(mContext, Constants.KEY_DEEPLINK_STATUS, Constants.DEEPLINK_STATE_FETCHED);
                    recoveryMemoryIfNeccesary();
                }
            }
        };

        WorkThreadManager.runInHttpThreadPool(runnable);
    }

    private void recoveryMemoryIfNeccesary() {
        mSpManager = null;
        sInstance = null;
    }
}
