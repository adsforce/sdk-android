package com.adsforce.sdk.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by t.wang on 2018/4/17.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class WorkThreadManager {
    private static Handler sMainHandler;
    private static Handler sWorkHandler;
    private static ExecutorService sHttpThreadPool = new ThreadPoolExecutor(1, 3,
            60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    static {
        initMainHandler();
        initWorkHandler();
    }

    private static void initWorkHandler() {
        if (sWorkHandler == null) {
            final HandlerThread thread = new HandlerThread("adsforce_sdk");
            thread.start();
            sWorkHandler = new Handler(thread.getLooper());
        }
    }

    private static void initMainHandler() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    public static void runInWorkThread(Runnable r) {
        initWorkHandler();

        sWorkHandler.post(r);
    }

    public static void runInWorkThread(Runnable r, long delay) {
        initWorkHandler();

        sWorkHandler.postDelayed(r, delay);
    }

    public static void removeInWorkThread(Runnable r) {
        initWorkHandler();

        sWorkHandler.removeCallbacks(r);
    }

    public static void runInHttpThreadPool(Runnable r) {
        sHttpThreadPool.execute(r);
    }
}
