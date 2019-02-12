package com.xhance.sdk.manager;

import android.content.Context;

import com.xhance.sdk.sender.XhanceBaseEntity;

import org.json.JSONObject;

public interface XhanceDataManager<T extends XhanceBaseEntity> {
    T getClientSendData(Context context);

    T getServerSendData(Context context);

    void onSendToClientSuccess(Context context, T entity);

    void onSendToClientFail(Context context, T entity);

    void onSendToServerSuccess(Context context, T entity);

    void onSendToServerFail(Context context, T entity);

    void onParseDeepLinkIfExist(Context context, JSONObject jsonObject);

    String getActionApiName();

    String getRequestFromSendData(Context context, T entity);
}
