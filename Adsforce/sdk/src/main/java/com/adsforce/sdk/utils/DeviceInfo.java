package com.adsforce.sdk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.adsforce.sdk.BuildConfig;
import com.adsforce.sdk.manager.SharedPreferencesManager;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class DeviceInfo {
    private static final Uri FB_ATTRIBUTION_ID_CONTENT_URI =
            Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider");
    private static final String FB_ATTRIBUTION_ID_COLUMN_NAME = "aid";

    private static DeviceInfo sDeviceInfo;
    private SharedPreferencesManager mSpManager;
    private String mAndroidId = "";
    private String mOpenId = "";
    private String mGaid = "";

    public static DeviceInfo getInstance() {
        if (sDeviceInfo == null) {
            synchronized (DeviceInfo.class) {
                if (sDeviceInfo == null) {
                    sDeviceInfo = new DeviceInfo();
                }
            }
        }

        return sDeviceInfo;
    }

    public DeviceInfo() {
        mSpManager = new SharedPreferencesManager(Constants.SP_SETTING_FILE_NAME);
    }

    public SortedMap<String, String> getDeviceInfoParams(Context context) {
        SortedMap<String, String> params = new TreeMap();
        params.put("aid", getAndroidId(context));
        params.put("oid", getOpenId(context));
        params.put("aon", getOsVersionName());
        params.put("aos", getOsVersionApi() + "");
        params.put("b", getBrand());
        params.put("n", NetUitls.getNetwork(context) + "");
        params.put("m", getBuildModel());
        params.put("pvc", getVersionCode(context) + "");
        params.put("pvn", getVersionName(context) + "");
        params.put("w", getDmWidth(context) + "");
        params.put("h", getDmHeight(context) + "");
        params.put("gaid", getGaid(context));
        params.put("appid", getPackageName(context) + "");
        params.put("lang", getLanguage(context));
        params.put("pkg", getPackageName(context) + "");
        params.put("f_cookie", getFacebookCookie(context) + "");
        params.put("local", getLocale() + "");
        params.put("tz_abb", getTzAbb() + "");
        params.put("tz", getTz() + "");
        params.put("carrier", getCarrier(context) + "");
        params.put("density", getDmDensity(context) + "");
        params.put("cpu_n", getCpuN() + "");
        params.put("ex_stg", getExStg() + "");
        params.put("av_stg", getAvStg() + "");
        params.put("build", getBuildId() + "");
        params.put("l_fp", "");
        params.put("sdk_ver", BuildConfig.VERSION_CODE + "");

        return params;
    }

    public void setAndroidId(Context context, String androidId) {
        try {
            mAndroidId = androidId;
            mSpManager.putString(context, Constants.KEY_SETTING_ANDROID_ID, androidId);
        } catch (Throwable t) {

        }
    }

    public String getAndroidId(Context context) {
        try {
            if (TextUtils.isEmpty(mAndroidId)) {
                mAndroidId = mSpManager.getString(context, Constants.KEY_SETTING_ANDROID_ID);
            }
        } catch (Throwable t) {

        } finally {
            return mAndroidId;
        }
    }

    public String getGaid(final Context context) {
        try {
            if (TextUtils.isEmpty(mGaid)) {
                mGaid = mSpManager.getString(context, Constants.KEY_SETTING_GAID);
            }
            if (TextUtils.isEmpty(mGaid)) {
                AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                mGaid = adInfo.getId();
                if (!TextUtils.isEmpty(mGaid)) {
                    mSpManager.putString(context, Constants.KEY_SETTING_GAID, mGaid);
                }
            }
        } catch (Throwable t) {

        } finally {
            return mGaid;
        }
    }

    public String getOpenId(Context context) {
        try {
            if (TextUtils.isEmpty(mOpenId)) {
                mOpenId = mSpManager.getString(context, Constants.KEY_SETTING_OPEN_ID);
            }
            if (TextUtils.isEmpty(mOpenId)) {
                mOpenId = Utils.getMd5Uuid();
                mSpManager.putString(context, Constants.KEY_SETTING_OPEN_ID, mOpenId);
            }
        } catch (Throwable t) {

        } finally {
            return mOpenId;
        }
    }

    public int getLadt(final Context context) {
        try {
            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            int optOutEnabled = adInfo.isLimitAdTrackingEnabled() ? 1 : 0;
            return optOutEnabled;
        } catch (Throwable t) {
            return -1;
        }
    }

    public String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Throwable e) {
            return "";
        }
    }

    public String getBrand() {
        String brand = "";
        try {
            brand = Build.MANUFACTURER.replace(" ", "_");
        } catch (Throwable e) {
        }
        return brand;
    }

    public String getBuildModel() {
        return Build.MODEL;
    }

    public String getBuildId() {
        return "Build/" + Build.ID;
    }

    public int getOsVersionApi() {
        int vesion = 0;
        try {
            vesion = android.os.Build.VERSION.SDK_INT;
        } catch (Throwable e) {
        }
        return vesion;
    }

    public String getOsVersionName() {
        String name = "";
        try {
            name = android.os.Build.VERSION.RELEASE;
        } catch (Throwable e) {
        }
        return name;
    }

    public String getLanguage(Context context) {
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            String language = locale.getDefault().toString();
            return language;
        } catch (Throwable e) {
            return "";
        }
    }

    public int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Throwable e) {
            return 0;
        }
    }

    public String getVersionName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA).versionName;
        } catch (Throwable e) {
            return "";
        }
    }

    public double getDmDensity(Context context) {
        try {
            return getDM(context).density;
        } catch (Throwable e) {
            return 0;
        }
    }

    public long getDmWidth(Context context) {
        try {
            return getDM(context).widthPixels;
        } catch (Throwable e) {
            return 0;
        }
    }

    public long getDmHeight(Context context) {
        try {
            return getDM(context).heightPixels;
        } catch (Throwable e) {
            return 0;
        }
    }

    public DisplayMetrics getDM(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        try {
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        } catch (Throwable e) {
        }
        return dm;
    }

    public boolean hasGP(Context context) {
        return pkgExists("com.android.vending", context);
    }

    public int hasGooleService(Context context) {
        int hasGoogleService;
        try {
            int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            if (resultCode == ConnectionResult.SUCCESS) {
                hasGoogleService = 1;
            } else {
                hasGoogleService = 0;
            }
        } catch (Throwable throwable) {
            hasGoogleService = -1;
        }

        return hasGoogleService;
    }

    public boolean hasFB(Context context) {
        return pkgExists("com.facebook.katana", context);
    }

    public String getFacebookCookie(Context context) {
        String[] projection = {FB_ATTRIBUTION_ID_COLUMN_NAME};
        Cursor c = context.getContentResolver().query(FB_ATTRIBUTION_ID_CONTENT_URI, projection, null, null, null);
        if (c == null || !c.moveToFirst()) {
            return "";
        }

        String attributionId = c.getString(c.getColumnIndex(FB_ATTRIBUTION_ID_COLUMN_NAME));
        c.close();
        return attributionId;
    }

    public boolean pkgExists(String pkgName, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public String getLocale() {
        try {
            return Locale.getDefault().toString();
        } catch (Throwable e) {
            return "";
        }
    }

    public String getTzAbb() {
        try {
            TimeZone tz = TimeZone.getDefault();
            return tz.getDisplayName(tz.inDaylightTime(new Date()), TimeZone.SHORT);
        } catch (Throwable e) {
            return "";
        }
    }

    public String getTz() {
        try {
            return TimeZone.getDefault().getID();
        } catch (Throwable e) {
            return "";
        }
    }

    public String getCarrier(Context context) {
        String carrier = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            carrier = telephonyManager.getNetworkOperatorName();
        } catch (Throwable e) {

        }
        return TextUtils.isEmpty(carrier) ? "NoCarrier" : carrier;
    }

    public int getCpuN() {
        int count = 0;
        try {
            count = Math.max(Runtime.getRuntime().availableProcessors(), 1);
        } catch (Throwable e) {
        }
        return count;
    }

    public long getAvStg() {
        long availableExternalStorage = 0;
        try {
            if (externalStorageExists()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                availableExternalStorage = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
            availableExternalStorage = convertBytesToGB(availableExternalStorage);
        } catch (Throwable e) {
        }
        return availableExternalStorage;
    }

    public long getExStg() {
        long totalExternalStorage = 0;
        try {
            if (externalStorageExists()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                totalExternalStorage = (long) stat.getBlockCount() * (long) stat.getBlockSize();
            }
            totalExternalStorage = convertBytesToGB(totalExternalStorage);
        } catch (Throwable e) {
        }
        return totalExternalStorage;
    }

    private boolean externalStorageExists() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private long convertBytesToGB(double bytes) {
        return Math.round(bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
