package com.xhance.sdk.event.session;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.xhance.sdk.utils.LogUtils;

public class XhanceLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static int sCount = 0;
    private static XhanceLifecycleCallbacks sLifecycleCallbacks;

    public static XhanceLifecycleCallbacks getInstance() {
        if (sLifecycleCallbacks == null) {
            synchronized (XhanceLifecycleCallbacks.class) {
                if (sLifecycleCallbacks == null) {
                    sLifecycleCallbacks = new XhanceLifecycleCallbacks();
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
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivityCreated ......");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivityStarted ......");

        if (sCount == 0) {
            LogUtils.info(">>>>>>>>>>>>>>>>>>>App switch to foreground>>>>>>>>>>>>>>>>>>>");
            XhanceSessionManager.getInstance().startSession();
        }
        sCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivityResumed ......");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivityPaused ......");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivityStopped ......");

        sCount--;
        if (sCount == 0) {
            LogUtils.info(">>>>>>>>>>>>>>>>>>>App switch to background>>>>>>>>>>>>>>>>>>>");
            XhanceSessionManager.getInstance().endSession();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivitySaveInstanceState ......");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.debug("AdcSdkLifecycleCallbacks onActivityDestroyed ......");
    }
}
