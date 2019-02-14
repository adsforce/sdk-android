package com.adsforce.sdk.manager;

import android.content.Context;

import com.adsforce.sdk.sender.AdsforceBaseEntity;

import org.json.JSONObject;

/**
 * Created by t.wang on 2018/7/20.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public interface AdsforceDataManager<T extends AdsforceBaseEntity> {
    T getClientSendData(Context context);

    T getServerSendData(Context context);

    void onSendToClientSuccess(Context context, T entity);

    void onSendToClientFail(Context context, T entity);

    void onSendToServerSuccess(Context context, T entity);

    void onSendToServerFail(Context context, T entity);

    void onParseDeepLinkIfExist(Context context, JSONObject jsonObject);

    String getDataSenderName();

    String getRequestFromSendData(Context context, T entity);
}
