package com.adsforce.sdk.event.session;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.adsforce.sdk.utils.LogUtils;

/**
 * Created by t.wang on 2018/5/14.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static int sCount = 0;
    private static AdsforceLifecycleCallbacks sLifecycleCallbacks;

    public static AdsforceLifecycleCallbacks getInstance() {
        if (sLifecycleCallbacks == null) {
            synchronized (AdsforceLifecycleCallbacks.class) {
                if (sLifecycleCallbacks == null) {
                    sLifecycleCallbacks = new AdsforceLifecycleCallbacks();
                }
            }
        }

        return sLifecycleCallbacks;
    }

    public boolean isAppForeground() {
        return sCount > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivityCreated ......");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivityStarted ......");

        if (sCount == 0) {
            LogUtils.info(">>>>>>>>>>>>>>>>>>>App switch to foreground>>>>>>>>>>>>>>>>>>>");
            AdsforceSessionManager.getInstance().startSession();
        }
        sCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivityResumed ......");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivityPaused ......");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivityStopped ......");

        sCount--;
        if (sCount == 0) {
            LogUtils.info(">>>>>>>>>>>>>>>>>>>App switch to background>>>>>>>>>>>>>>>>>>>");
            AdsforceSessionManager.getInstance().endSession();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivitySaveInstanceState ......");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.debug("AdsforceLifecycleCallbacks onActivityDestroyed ......");
    }
}
