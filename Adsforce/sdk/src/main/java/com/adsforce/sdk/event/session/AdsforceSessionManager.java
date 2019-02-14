package com.adsforce.sdk.event.session;

import android.app.Application;
import android.os.Build;

import com.adsforce.sdk.event.AdsforceEventEntity;
import com.adsforce.sdk.event.AdsforceEventManager;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.LogUtils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by t.wang on 2018/5/29.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceSessionManager {
    private static final int SESSION_TIMER_SECONDS = 5 * 60;

    private static AdsforceSessionManager sAdsforceSessionManager;
    private AdsforceEventManager mEventManager;

    private ScheduledThreadPoolExecutor mScheduledThread;
    private static boolean sLifecycleRegistered;

    private static final String DATA_TYPE_OPEN = "s_open";
    private static final String DATA_TYPE_START = "s_start";
    private static final String DATA_TYPE_TIMER = "s_going";
    private static final String DATA_TYPE_END = "s_end";

    public static AdsforceSessionManager getInstance() {
        if (sAdsforceSessionManager == null) {
            synchronized (AdsforceSessionManager.class) {
                if (sAdsforceSessionManager == null) {
                    sAdsforceSessionManager = new AdsforceSessionManager();
                }
            }
        }

        return sAdsforceSessionManager;
    }

    public void init(Application application) {
        if (mEventManager == null) {
            mEventManager = AdsforceEventManager.getInstance();
            mEventManager.init(application);
        }

        if (mScheduledThread == null) {
            mScheduledThread = new ScheduledThreadPoolExecutor(1);
        }

        openSession();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !sLifecycleRegistered) {
            application.registerActivityLifecycleCallbacks(AdsforceLifecycleCallbacks.getInstance());
            sLifecycleRegistered = true;
        }
    }

    private void openSession() {
        AdsforceEventEntity data = new AdsforceEventEntity();
        data.setEventCategory(Constants.CATEGORY_SESSION);
        data.setEventType(DATA_TYPE_OPEN);
        data.setClientTime(System.currentTimeMillis());
        mEventManager.saveAndSendEventDataArray(data);
    }

    protected void startSession() {
        AdsforceEventEntity data = new AdsforceEventEntity();
        data.setEventCategory(Constants.CATEGORY_SESSION);
        data.setEventType(DATA_TYPE_START);
        data.setClientTime(System.currentTimeMillis());
        if (mEventManager.isLastSessionStartOverTime()) {
            LogUtils.debug("session_start event over 5 mins");
            mEventManager.saveAndSendEventDataArray(data);
        } else {
            LogUtils.debug("session_start event less than 5 mins");
        }
        startTimer();
    }

    protected void endSession() {
        AdsforceEventEntity data = new AdsforceEventEntity();
        data.setEventCategory(Constants.CATEGORY_SESSION);
        data.setEventType(DATA_TYPE_END);
        data.setClientTime(System.currentTimeMillis());
        if (mEventManager.isLastSessionEndOverTime()) {
            LogUtils.debug("session_end event over 5 mins");
            mEventManager.saveAndSendEventDataArray(data);
        } else {
            LogUtils.debug("session_end event less than 5 mins");
        }
        endTimer();
    }

    private void timerSession() {
        AdsforceEventEntity data = new AdsforceEventEntity();
        data.setEventCategory(Constants.CATEGORY_SESSION);
        data.setEventType(DATA_TYPE_TIMER);
        data.setClientTime(System.currentTimeMillis());
        mEventManager.saveAndSendEventDataArray(data);
    }

    private void startTimer() {
        if (mScheduledThread == null) {
            mScheduledThread = new ScheduledThreadPoolExecutor(1);
        }

        try {
            mScheduledThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (isAppForeground()) {
                        timerSession();
                    }
                }
            }, SESSION_TIMER_SECONDS, SESSION_TIMER_SECONDS, TimeUnit.SECONDS);
        } catch (Throwable throwable) {
            LogUtils.warn("mScheduledThread.scheduleAtFixedRate failed, " + throwable.getMessage(), throwable);
        }
    }

    private void endTimer() {
        if (mScheduledThread != null) {
            mScheduledThread.shutdownNow();
            mScheduledThread = null;
        }
    }

    private boolean isAppForeground() {
        return AdsforceLifecycleCallbacks.getInstance().isAppForeground();
    }

}
