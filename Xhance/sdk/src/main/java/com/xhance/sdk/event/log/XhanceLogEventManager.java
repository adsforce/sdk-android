package com.xhance.sdk.event.log;

import android.content.Context;

import com.xhance.sdk.event.XhanceEventEntity;
import com.xhance.sdk.event.XhanceEventManager;
import com.xhance.sdk.utils.Constants;
import com.xhance.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class XhanceLogEventManager {
    private static XhanceLogEventManager sXhanceLogEventManager;
    private XhanceEventManager mEventManager;

    public static XhanceLogEventManager getInstance() {
        if (sXhanceLogEventManager == null) {
            synchronized (XhanceLogEventManager.class) {
                if (sXhanceLogEventManager == null) {
                    sXhanceLogEventManager = new XhanceLogEventManager();
                }
            }
        }

        return sXhanceLogEventManager;
    }

    public void init(Context context) {
        if (mEventManager == null) {
            mEventManager = XhanceEventManager.getInstance();
            mEventManager.init(context);
        }
    }

    public void logEventByValue(String key, String value) {
        XhanceEventEntity data = new XhanceEventEntity();
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
