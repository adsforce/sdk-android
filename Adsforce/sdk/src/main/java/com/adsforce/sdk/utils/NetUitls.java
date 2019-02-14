package com.adsforce.sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by t.wang on 2018/4/16.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class NetUitls {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_2G = 2;
    public static final int TYPE_3G = 3;
    public static final int TYPE_4G = 4;

    public static boolean isNetworkAvailable(Context context) {
        return isNetworkAvailable(getActiveNetworkInfo(context));
    }

    public static boolean isNetworkAvailable(NetworkInfo activeNetwork) {
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo();
        } catch (Throwable e) {
            return null;
        }
    }

    public static int getNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(TYPE_WIFI);
        if (info != null && info.isConnectedOrConnecting()) {
            return TYPE_WIFI;
        } else {
            info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != info && info.isAvailable() && info.isConnectedOrConnecting()) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                        return TYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return TYPE_4G;
                    default:
                        return TYPE_UNKNOWN;
                }
            } else {
                return TYPE_NONE;
            }
        }
    }
}
