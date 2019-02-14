package com.adsforce.sdk.event.log;

import android.content.Context;

import com.adsforce.sdk.event.AdsforceEventEntity;
import com.adsforce.sdk.event.AdsforceEventManager;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by t.wang on 2018/5/29.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceLogEventManager {
    private static AdsforceLogEventManager sAdsforceLogEventManager;
    private AdsforceEventManager mEventManager;

    public static AdsforceLogEventManager getInstance() {
        if (sAdsforceLogEventManager == null) {
            synchronized (AdsforceLogEventManager.class) {
                if (sAdsforceLogEventManager == null) {
                    sAdsforceLogEventManager = new AdsforceLogEventManager();
                }
            }
        }

        return sAdsforceLogEventManager;
    }

    public void init(Context context) {
        if (mEventManager == null) {
            mEventManager = AdsforceEventManager.getInstance();
            mEventManager.init(context);
        }
    }

    public void logEventByValue(String key, String value) {
        AdsforceEventEntity data = new AdsforceEventEntity();
        data.setEventCategory(Constants.CATEGORY_EVENT);
        data.setEventType(key);
        data.setEventValue(value);
        data.setClientTime(System.currentTimeMillis());
        mEventManager.saveAndSendEventDataArray(data);
    }

    public void logEventByMap(String key, HashMap<String, String> map) {
        String value = "";
        if (map != null && !map.isEmpty()) {
            value = Utils.convertMapToJson(map);
        }
        logEventByValue(key, value);
    }

    public void logEventByList(String key, ArrayList<String> list) {
        String value = "";
        if (list != null && !list.isEmpty()) {
            value = Utils.convertListToJson(list);
        }
        logEventByValue(key, value);
    }
}
