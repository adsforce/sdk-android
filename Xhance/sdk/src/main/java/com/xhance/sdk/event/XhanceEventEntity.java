package com.xhance.sdk.event;

import com.xhance.sdk.sender.XhanceBaseEntity;
import com.xhance.sdk.utils.Constants;

import org.json.JSONObject;

public class XhanceEventEntity extends XhanceBaseEntity {
    private String mDataId;
    private String mSessionId;
    private String mEventCategory;
    private String mEventType;
    private String mEventValue;
    private String mRevnPrice;
    private String mRevnCurr;
    private String mRevnItemId;
    private String mRevnItemCat;
    private String mRevnPubKey;
    private String mRevnSign;
    private String mRevnPurchaseData;
    private String mRevnParams;

    public XhanceEventEntity() {
    }

    public XhanceEventEntity(JSONObject jsonObject) {
        mDataId = jsonObject.optString(Constants.KEY_EVENT_DATA_ID);
        mSessionId = jsonObject.optString(Constants.KEY_EVENT_SESSION_ID);
        mClientTime = jsonObject.optLong(Constants.KEY_EVENT_CLIENT_TIME);
        mEventCategory = jsonObject.optString(Constants.KEY_EVENT_CATEGORY);
        mEventType = jsonObject.optString(Constants.KEY_EVENT_TYPE);
        mEventValue = jsonObject.optString(Constants.KEY_EVENT_VALUE);
        mRevnPrice = jsonObject.optString(Constants.KEY_REVENUE_PRICE);
        mRevnCurr = jsonObject.optString(Constants.KEY_REVENUE_CURR);
        mRevnItemId = jsonObject.optString(Constants.KEY_REVENUE_ITEM_ID);
        mRevnItemCat = jsonObject.optString(Constants.KEY_REVENUE_ITEM_CAT);
        mRevnPubKey = jsonObject.optString(Constants.KEY_REVENUE_PUB_KEY);
        mRevnSign = jsonObject.optString(Constants.KEY_REVENUE_SIGN);
        mRevnPurchaseData = jsonObject.optString(Constants.KEY_REVENUE_PURCHASE_DATA);
        mRevnParams = jsonObject.optString(Constants.KEY_REVENUE_PARAMS);
        mSendCount = jsonObject.optInt(Constants.KEY_EVENT_SEND_COUNT);
        mSended = jsonObject.optBoolean(Constants.KEY_EVENT_DATA_SENDED);
        mUuid = jsonObject.optString(Constants.KEY_UUID);
    }

    public JSONObject convertToJson() throws Throwable {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.KEY_EVENT_DATA_ID, getDataId());
        jsonObject.put(Constants.KEY_EVENT_SESSION_ID, getSessionId());
        jsonObject.put(Constants.KEY_EVENT_CLIENT_TIME, getClientTime());
        jsonObject.put(Constants.KEY_EVENT_CATEGORY, getEventCategory());
        jsonObject.put(Constants.KEY_EVENT_TYPE, getEventType());
        jsonObject.put(Constants.KEY_EVENT_VALUE, getEventValue());
        jsonObject.put(Constants.KEY_REVENUE_PRICE, getRevnPrice());
        jsonObject.put(Constants.KEY_REVENUE_CURR, getRevnCurr());
        jsonObject.put(Constants.KEY_REVENUE_ITEM_ID, getRevnItemId());
        jsonObject.put(Constants.KEY_REVENUE_ITEM_CAT, getRevnItemCat());
        jsonObject.put(Constants.KEY_REVENUE_PUB_KEY, getRevnPubKey());
        jsonObject.put(Constants.KEY_REVENUE_SIGN, getRevnSign());
        jsonObject.put(Constants.KEY_REVENUE_PURCHASE_DATA, getRevnPurchaseData());
        jsonObject.put(Constants.KEY_REVENUE_PARAMS, getRevnParams());
        jsonObject.put(Constants.KEY_EVENT_SEND_COUNT, getSendCount());
        jsonObject.put(Constants.KEY_EVENT_DATA_SENDED, isSended());
        jsonObject.put(Constants.KEY_UUID, getUuid());
        return jsonObject;
    }

    public String getDataId() {
        return mDataId;
    }

    public void setDataId(String dataId) {
        mDataId = dataId;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        this.mSessionId = sessionId;
    }

    public String getEventType() {
        return mEventType;
    }

    public void setEventType(String eventType) {
        this.mEventType = eventType;
    }

    public String getEventValue() {
        return mEventValue;
    }

    public void setEventValue(String eventValue) {
        mEventValue = eventValue;
    }

    public String getEventCategory() {
        return mEventCategory;
    }

    public void setEventCategory(String eventCategory) {
        mEventCategory = eventCategory;
    }

    public String getRevnPrice() {
        return mRevnPrice;
    }

    public void setRevnPrice(String revnPrice) {
        mRevnPrice = revnPrice;
    }

    public String getRevnCurr() {
        return mRevnCurr;
    }

    public void setRevnCurr(String RevnCurr) {
        mRevnCurr = RevnCurr;
    }

    public String getRevnItemId() {
        return mRevnItemId;
    }

    public void setRevnItemId(String RevnItemId) {
        mRevnItemId = RevnItemId;
    }

    public String getRevnItemCat() {
        return mRevnItemCat;
    }

    public void setRevnItemCat(String RevnItemCat) {
        mRevnItemCat = RevnItemCat;
    }

    public String getRevnPubKey() {
        return mRevnPubKey;
    }

    public void setRevnPubKey(String RevnPubKey) {
        mRevnPubKey = RevnPubKey;
    }

    public String getRevnSign() {
        return mRevnSign;
    }

    public void setRevnSign(String RevnSign) {
        mRevnSign = RevnSign;
    }

    public String getRevnPurchaseData() {
        return mRevnPurchaseData;
    }

    public void setRevnPurchaseData(String RevnPurchaseData) {
        mRevnPurchaseData = RevnPurchaseData;
    }

    public String getRevnParams() {
        return mRevnParams;
    }

    public void setRevnParams(String RevnParams) {
        mRevnParams = RevnParams;
    }

    @Override
    public String toString() {
        try {
            return this.convertToJson().toString();
        } catch (Throwable throwable) {

        }
        return "";
    }
}
