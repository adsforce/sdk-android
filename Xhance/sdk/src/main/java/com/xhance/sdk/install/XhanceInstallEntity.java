package com.xhance.sdk.install;

import com.xhance.sdk.sender.XhanceBaseEntity;
import com.xhance.sdk.utils.Constants;

import org.json.JSONObject;

public class XhanceInstallEntity extends XhanceBaseEntity {
    private String mReffer;
    private long mReferrerClickTime;
    private long mInstallClickTime;

    public XhanceInstallEntity() {
    }

    public XhanceInstallEntity(JSONObject jsonObject) {
        mReffer = jsonObject.optString(Constants.KEY_INSTALL_REFERRER);
        mClientTime = jsonObject.optLong(Constants.KEY_INSTALL_CLIENT_TIME);
        mReferrerClickTime = jsonObject.optLong(Constants.KEY_INSTALL_REFFER_CLICK_TIME);
        mInstallClickTime = jsonObject.optLong(Constants.KEY_INSTALL_INSTALL_CLICK_TIME);
        mSended = jsonObject.optBoolean(Constants.KEY_INSTALL_DATA_SENDED);
        mSendCount = jsonObject.optInt(Constants.KEY_INSTALL_SEND_COUNT);
        mUuid = jsonObject.optString(Constants.KEY_UUID);
    }

    public JSONObject convertToJson() throws Throwable {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.KEY_INSTALL_REFERRER, getReffer());
        jsonObject.put(Constants.KEY_INSTALL_CLIENT_TIME, getClientTime());
        jsonObject.put(Constants.KEY_INSTALL_REFFER_CLICK_TIME, getReferrerClickTime());
        jsonObject.put(Constants.KEY_INSTALL_INSTALL_CLICK_TIME, getInstallClickTime());
        jsonObject.put(Constants.KEY_INSTALL_DATA_SENDED, isSended());
        jsonObject.put(Constants.KEY_INSTALL_SEND_COUNT, getSendCount());
        jsonObject.put(Constants.KEY_UUID, getUuid());
        return jsonObject;
    }

    public String getReffer() {
        return mReffer;
    }

    public void setReffer(String reffer) {
        mReffer = reffer;
    }

    public long getReferrerClickTime() {
        return mReferrerClickTime;
    }

    public void setReferrerClickTime(long referrerClickTime) {
        mReferrerClickTime = referrerClickTime;
    }

    public long getInstallClickTime() {
        return mInstallClickTime;
    }

    public void setInstallClickTime(long installClickTime) {
        mInstallClickTime = installClickTime;
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
