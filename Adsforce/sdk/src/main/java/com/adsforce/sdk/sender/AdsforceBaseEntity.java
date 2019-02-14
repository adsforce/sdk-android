package com.adsforce.sdk.sender;

/**
 * Created by t.wang on 2018/7/20.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public abstract class AdsforceBaseEntity {
    protected long mClientTime;
    protected boolean mSended;
    protected int mSendCount;
    protected String mUuid;

    public abstract String getEventCategory();

    public long getClientTime() {
        return mClientTime;
    }

    public void setClientTime(long clientTime) {
        this.mClientTime = clientTime;
    }

    public boolean isSended() {
        return mSended;
    }

    public void setSended(boolean sended) {
        mSended = sended;
    }

    public int getSendCount() {
        return mSendCount;
    }

    public void setSendCount(int sendCount) {
        mSendCount = sendCount;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    public String getUuid() {
        return mUuid;
    }
}
