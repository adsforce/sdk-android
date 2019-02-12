package com.xhance.sdk.event.session;

import android.app.Application;
import android.os.Build;

import com.xhance.sdk.event.XhanceEventEntity;
import com.xhance.sdk.event.XhanceEventManager;
import com.xhance.sdk.utils.Constants;
import com.xhance.sdk.utils.LogUtils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class XhanceSessionManager {
    private static final int SESSION_TIMER_SECONDS = 5 * 60;

    private static XhanceSessionManager sXhanceSessionManager;
    private XhanceEventManager mEventManager;

    private ScheduledThreadPoolExecutor mScheduledThread;
    private static boolean sLifecycleRegistered;

    private static final String DATA_TYPE_START = "s_start";
    private static final String DATA_TYPE_TIMER = "s_going";
    private static final String DATA_TYPE_END = "s_end";

    public static XhanceSessionManager getInstance() {
        if (sXhanceSessionManager == null) {
            synchronized (XhanceSessionManager.class) {
                if (sXhanceSessionManager == null) {
                    sXhanceSessionManager = new XhanceSessionManager();
                }
            }
        }

        return sXhanceSessionManager;
    }

    public void init(Application application) {
        if (mEventManager == null) {
            mEventManager = XhanceEventManager.getInstance();
            mEventManager.init(application);
        }

        if (mScheduledThread == null) {
            mScheduledThread = new ScheduledThreadPoolExecutor(1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !sLifecycleRegistered) {
            application.registerActivityLifecycleCallbacks(XhanceLifecycleCallbacks.getInstance());
            sLifecycleRegistered = true;
        }
    }

    protected void startSession() {
        XhanceEventEntity data = new XhanceEventEntity();
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
        XhanceEventEntity data = new XhanceEventEntity();
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
        XhanceEventEntity data = new XhanceEventEntity();
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
        return XhanceLifecycleCallbacks.getInstance().isAppForeground();
    }

}
