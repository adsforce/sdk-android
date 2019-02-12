package com.xhance.sdk.event;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.xhance.sdk.XhanceSdk;
import com.xhance.sdk.config.XhanceSdkConfig;
import com.xhance.sdk.manager.SharedPreferencesManager;
import com.xhance.sdk.manager.WorkThreadManager;
import com.xhance.sdk.manager.XhanceDataManager;
import com.xhance.sdk.sender.XhanceDataSender;
import com.xhance.sdk.sender.XhanceDataSenderFactory;
import com.xhance.sdk.utils.Constants;
import com.xhance.sdk.utils.DeviceInfo;
import com.xhance.sdk.utils.LogUtils;
import com.xhance.sdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.UUID;

import static com.xhance.sdk.utils.Constants.KEY_EVENT_DATA_CLIENT_ARRAY;
import static com.xhance.sdk.utils.Constants.KEY_EVENT_DATA_SERVER_ARRAY;
import static com.xhance.sdk.utils.Constants.KEY_EVENT_LAST_SESSION_END_TIME;
import static com.xhance.sdk.utils.Constants.KEY_EVENT_LAST_SESSION_ID;
import static com.xhance.sdk.utils.Constants.KEY_EVENT_LAST_SESSION_START_TIME;
import static com.xhance.sdk.utils.Constants.KEY_EVNET_LAST_ACTIVE_TIME;

public class XhanceEventManager implements XhanceDataManager<XhanceEventEntity> {
    private static XhanceEventManager sEventManager;
    private XhanceDataSender mEventSender;
    private SharedPreferencesManager mSpManager;

    private Context mContext;
    private JSONArray mClientJsonArray;
    private JSONArray mServerJsonArray;

    public static XhanceEventManager getInstance() {
        if (sEventManager == null) {
            synchronized (XhanceEventManager.class) {
                if (sEventManager == null) {
                    sEventManager = new XhanceEventManager();
                }
            }
        }

        return sEventManager;
    }

    public void init(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (mSpManager == null) {
            mSpManager = new SharedPreferencesManager(Constants.SP_EVENT_FILE_NAME);
        }
        if (mEventSender == null) {
            mEventSender = XhanceDataSenderFactory.getXhanceDataSender(getActionApiName());
            mEventSender.setDataManager(this);
        }
    }

    public boolean isLastSessionStartOverTime() {
        long lastSessionStartTime = mSpManager.getLong(mContext, KEY_EVENT_LAST_SESSION_START_TIME);
        if (System.currentTimeMillis() - lastSessionStartTime > Constants.FIVE_MINUTES) {
            mSpManager.putLong(mContext, KEY_EVENT_LAST_SESSION_START_TIME, System.currentTimeMillis());
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastSessionEndOverTime() {
        long lastSessionEndTime = mSpManager.getLong(mContext, KEY_EVENT_LAST_SESSION_END_TIME);
        if (System.currentTimeMillis() - lastSessionEndTime > Constants.FIVE_MINUTES) {
            mSpManager.putLong(mContext, KEY_EVENT_LAST_SESSION_END_TIME, System.currentTimeMillis());
            return true;
        } else {
            return false;
        }
    }

    public void saveAndSendEventDataArray(final XhanceEventEntity eventData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    updateSessionIdAndActiveTime(eventData);
                    saveAndSendClientData(eventData);
                    saveAndSendServerData(eventData);
                } catch (Throwable throwable) {
                    LogUtils.warn("saveAndSendEventDataArray failed, " + throwable.getMessage(), throwable);
                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    private void updateSessionIdAndActiveTime(XhanceEventEntity eventData) {
        long lastActivitiesTime = mSpManager.getLong(mContext, KEY_EVNET_LAST_ACTIVE_TIME);
        String sessionId;
        if (System.currentTimeMillis() - lastActivitiesTime > Constants.SESSION_OUT_TIME_MILLS) {
            sessionId = Utils.getMd5Uuid();
            mSpManager.putString(mContext, KEY_EVENT_LAST_SESSION_ID, sessionId);
        } else {
            sessionId = mSpManager.getString(mContext, KEY_EVENT_LAST_SESSION_ID);
        }
        eventData.setSessionId(sessionId);
        if (TextUtils.isEmpty(eventData.getUuid())) {
            eventData.setUuid(Utils.getMd5Uuid());
        }
        mSpManager.putLong(mContext, KEY_EVNET_LAST_ACTIVE_TIME, System.currentTimeMillis());
    }

    private void saveAndSendClientData(XhanceEventEntity eventData) throws Throwable {
        if (mClientJsonArray == null) {
            synchronized (this) {
                if (mClientJsonArray == null) {
                    mClientJsonArray = getEventDataArray(mContext, KEY_EVENT_DATA_CLIENT_ARRAY);
                }
            }
        }
        eventData.setDataId(UUID.randomUUID().toString());
        synchronized (mClientJsonArray) {
            mClientJsonArray.put(eventData.convertToJson());
        }
        saveEventDataArray(mContext, KEY_EVENT_DATA_CLIENT_ARRAY, mClientJsonArray.toString());
        mEventSender.sendDataToClient(mContext);
    }

    private void saveAndSendServerData(XhanceEventEntity eventData) throws Throwable {
        if (mServerJsonArray == null) {
            synchronized (this) {
                if (mServerJsonArray == null) {
                    mServerJsonArray = getEventDataArray(mContext, KEY_EVENT_DATA_SERVER_ARRAY);
                }
            }
        }
        eventData.setDataId(UUID.randomUUID().toString());
        synchronized (mServerJsonArray) {
            mServerJsonArray.put(eventData.convertToJson());
        }
        saveEventDataArray(mContext, KEY_EVENT_DATA_SERVER_ARRAY, mServerJsonArray.toString());
        mEventSender.sendDataToServer(mContext);
    }

    public XhanceEventEntity getClientSendData(Context context) {
        try {
            if (mClientJsonArray == null || mClientJsonArray.length() == 0) {
                LogUtils.warn("event_client_data, no data waiting to send", null);
                return null;
            } else {
                LogUtils.debug("event_client_data is " + mClientJsonArray);
            }

            synchronized (mClientJsonArray) {
                for (int i = 0; i < mClientJsonArray.length(); i++) {
                    JSONObject eventJson = (JSONObject) mClientJsonArray.get(i);
                    XhanceEventEntity eventData = new XhanceEventEntity(eventJson);
                    if (eventData.isSended() || eventData.getSendCount() >= Constants.SEND_MAX_COUNTS) {
                        continue;
                    } else {
                        return eventData;
                    }
                }
            }
        } catch (Throwable throwable) {

        }

        return null;
    }

    public XhanceEventEntity getServerSendData(Context context) {
        try {
            if (mServerJsonArray == null || mServerJsonArray.length() == 0) {
                LogUtils.warn("event_server_data, no data waiting to send", null);
                return null;
            } else {
                LogUtils.debug("event_server_data is " + mServerJsonArray);
            }

            synchronized (mServerJsonArray) {
                for (int i = 0; i < mServerJsonArray.length(); i++) {
                    JSONObject eventJson = (JSONObject) mServerJsonArray.get(i);
                    XhanceEventEntity eventData = new XhanceEventEntity(eventJson);
                    if (eventData.isSended() || eventData.getSendCount() >= Constants.SEND_MAX_COUNTS) {
                        continue;
                    } else {
                        return eventData;
                    }
                }
            }
        } catch (Throwable throwable) {

        }

        return null;
    }

    private JSONArray getEventDataArray(Context context, String arrayKey) {
        try {
            String eventDataArray = mSpManager.getString(context, arrayKey);
            if (!TextUtils.isEmpty(eventDataArray)) {
                JSONArray jsonArray = new JSONArray(eventDataArray);
                return jsonArray;
            }
        } catch (Throwable throwable) {
            LogUtils.warn("getEventDataArray failed, " + throwable.getMessage(), throwable);
        }

        return new JSONArray();
    }

    private void saveEventDataArray(Context context, String arrayKey, String eventDataArray) {
        if (eventDataArray != null) {
            try {
                JSONArray jsonArray = new JSONArray(eventDataArray);
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONArray unSendedArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        if (object.optBoolean(Constants.KEY_EVENT_DATA_SENDED)) {
                            continue;
                        }
                        if (object.optInt(Constants.KEY_EVENT_SEND_COUNT) >= Constants.SEND_MAX_COUNTS) {
                            continue;
                        }
                        unSendedArray.put(object);
                    }
                    mSpManager.putString(context, arrayKey, unSendedArray.toString());
                }
            } catch (Throwable throwable) {
                LogUtils.warn("saveEventDataArray failed, " + throwable.getMessage(), throwable);
            }
        }
    }

    private JSONArray removeJsonObjectAtIndex(JSONArray jsonArray, int index) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            jsonArray.remove(index);
            return jsonArray;
        } else {
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i == index) {
                    continue;
                }
                JSONObject object = (JSONObject) jsonArray.get(i);
                newArray.put(object);
            }
            return newArray;
        }
    }

    public void onSendToClientSuccess(final Context context, final XhanceEventEntity eventData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mClientJsonArray != null) {
                        synchronized (mClientJsonArray) {
                            for (int i = 0; i < mClientJsonArray.length(); i++) {
                                JSONObject eventJson = (JSONObject) mClientJsonArray.get(i);
                                if (eventJson.optString(Constants.KEY_EVENT_DATA_ID).equals(eventData.getDataId())) {
                                    mClientJsonArray = removeJsonObjectAtIndex(mClientJsonArray, i);
                                    break;
                                }
                            }
                            saveEventDataArray(context, KEY_EVENT_DATA_CLIENT_ARRAY, mClientJsonArray.toString());
                        }
                    }
                } catch (Throwable throwable) {

                } finally {
                    mEventSender.sendDataToClient(context);
                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onSendToClientFail(final Context context, final XhanceEventEntity eventData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mClientJsonArray != null) {
                        synchronized (mClientJsonArray) {
                            for (int i = 0; i < mClientJsonArray.length(); i++) {
                                JSONObject eventJson = (JSONObject) mClientJsonArray.get(i);
                                if (eventJson.optString(Constants.KEY_EVENT_DATA_ID).equals(eventData.getDataId())) {
                                    int count = eventJson.optInt(Constants.KEY_EVENT_SEND_COUNT);
                                    count = count + 1;
                                    eventJson.put(Constants.KEY_EVENT_SEND_COUNT, count);
                                    break;
                                }
                            }
                            saveEventDataArray(context, KEY_EVENT_DATA_CLIENT_ARRAY, mClientJsonArray.toString());
                        }
                    }
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onSendToServerSuccess(final Context context, final XhanceEventEntity eventData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mServerJsonArray != null) {
                        synchronized (mServerJsonArray) {
                            for (int i = 0; i < mServerJsonArray.length(); i++) {
                                JSONObject eventJson = (JSONObject) mServerJsonArray.get(i);
                                if (eventJson.optString(Constants.KEY_EVENT_DATA_ID).equals(eventData.getDataId())) {
                                    mServerJsonArray = removeJsonObjectAtIndex(mServerJsonArray, i);
                                    break;
                                }
                            }
                            saveEventDataArray(context, KEY_EVENT_DATA_SERVER_ARRAY, mServerJsonArray.toString());
                        }
                    }
                } catch (Throwable throwable) {

                } finally {
                    mEventSender.sendDataToServer(context);
                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onSendToServerFail(final Context context, final XhanceEventEntity eventData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mServerJsonArray != null) {
                        synchronized (mServerJsonArray) {

                            for (int i = 0; i < mServerJsonArray.length(); i++) {
                                JSONObject eventJson = (JSONObject) mServerJsonArray.get(i);
                                if (eventJson.optString(Constants.KEY_EVENT_DATA_ID).equals(eventData.getDataId())) {
                                    int count = eventJson.optInt(Constants.KEY_EVENT_SEND_COUNT);
                                    count = count + 1;
                                    eventJson.put(Constants.KEY_EVENT_SEND_COUNT, count);
                                    break;
                                }
                            }
                            saveEventDataArray(context, KEY_EVENT_DATA_SERVER_ARRAY, mServerJsonArray.toString());
                        }
                    }
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onParseDeepLinkIfExist(Context context, JSONObject jsonObject) {
        return;
    }

    public String getActionApiName() {
        return Constants.ACTION_EVENT;
    }

    public String getRequestFromSendData(Context context, XhanceEventEntity eventData) {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        XhanceSdkConfig config = XhanceSdk.getSdkConfig();
        SortedMap<String, String> params = deviceInfo.getDeviceInfoParams(context);
        params.put("cat", eventData.getEventCategory());
        params.put("devkey", config.getDevKey());
        params.put("cid", config.getChannelId());
        params.put("cts", eventData.getClientTime() + "");

        String eventCategory = eventData.getEventCategory();
        if (eventCategory.equals(Constants.CATEGORY_REVENUE)) {
            params.put("revn", eventData.getRevnPrice());
            params.put("revn_curr", eventData.getRevnCurr());
            params.put("item_id", eventData.getRevnItemId());
            params.put("item_cat", eventData.getRevnItemCat());
        } else if (eventCategory.equals(Constants.CATEGORY_REVENUE_VERIFY)) {
            params.put("revn", eventData.getRevnPrice());
            params.put("revn_curr", eventData.getRevnCurr());
            params.put("pubkey", eventData.getRevnPubKey());
            params.put("sign", eventData.getRevnSign());
            params.put("data", eventData.getRevnPurchaseData());
            params.put("params", eventData.getRevnParams());
        } else if (eventCategory.equals(Constants.CATEGORY_SESSION)) {
            params.put("s_id", eventData.getSessionId());
            params.put("e_id", eventData.getEventType());
        } else if (eventCategory.equals(Constants.CATEGORY_EVENT)) {
            params.put("e_id", eventData.getEventType());
            params.put("val", eventData.getEventValue());
        } else {
            return "";
        }

        String request = Utils.convertMapToString(params);
        return request;
    }
}
