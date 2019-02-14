package com.adsforce.sdk.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class SharedPreferencesManager {
    private String mFileName;

    public SharedPreferencesManager(String fileName) {
        mFileName = fileName;
    }

    public void putString(Context context, String key, String value) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public String getString(Context context, String key) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getString(key, null);
        }
    }

    public void putBoolean(Context context, String key, boolean value) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public boolean getBoolean(Context context, String key) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getBoolean(key, false);
        }
    }

    public boolean getBoolean(Context context, String key, boolean defaultValue) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getBoolean(key, defaultValue);
        }
    }

    public void putInt(Context context, String key, int value) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public int getInt(Context context, String key) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getInt(key, 0);
        }
    }

    public int getInt(Context context, String key, int defaultValue) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getInt(key, defaultValue);
        }
    }

    public void putLong(Context context, String key, long value) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public long getLong(Context context, String key) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getLong(key, 0);
        }
    }

    public long getLong(Context context, String key, long defaultValue) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            return sp.getLong(key, defaultValue);
        }
    }

    public void removeKey(Context context, String key) {
        synchronized (this) {
            SharedPreferences sp = context.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        }
    }

}
