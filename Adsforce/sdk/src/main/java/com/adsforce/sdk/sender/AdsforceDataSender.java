package com.adsforce.sdk.sender;

import android.content.Context;

import com.adsforce.sdk.http.callback.Callback;
import com.adsforce.sdk.manager.WorkThreadManager;
import com.adsforce.sdk.manager.AdsforceDataManager;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.LogUtils;
import com.adsforce.sdk.utils.NetUitls;

import org.json.JSONObject;

/**
 * Created by t.wang on 2018/7/20.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceDataSender {
    private AdsforceDataManager mDataManager;
    private boolean mClientSending;
    private Runnable mClientDelayRunnable;
    private boolean mServerSending;
    private Runnable mServerDelayRunnable;

    public void setDataManager(AdsforceDataManager dataManager) {
        mDataManager = dataManager;
    }

    public void sendDataToClient(final Context context) {
        try {
            if (mClientSending) {
                LogUtils.warn("sendDataToClient, data is sending", null);
                return;
            }

            final AdsforceBaseEntity sendData = mDataManager.getClientSendData(context);
            if (sendData == null) {
                LogUtils.warn("sendDataToClient, no data waiting to send", null);
                return;
            }

            if (!NetUitls.isNetworkAvailable(context)) {
                if (mClientDelayRunnable == null) {
                    mClientDelayRunnable = new Runnable() {
                        @Override
                        public void run() {
                            sendDataToClient(context);
                        }
                    };
                }
                WorkThreadManager.removeInWorkThread(mClientDelayRunnable);
                WorkThreadManager.runInWorkThread(mClientDelayRunnable, Constants.SEND_RETRY_MILLS);
                return;
            } else {
                if (mClientDelayRunnable != null) {
                    WorkThreadManager.removeInWorkThread(mClientDelayRunnable);
                    mClientDelayRunnable = null;
                }
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Callback callback = new Callback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            try {
                                mClientSending = false;
                                JSONObject jsonObject = new JSONObject(data);
                                if (jsonObject != null && jsonObject.optInt("ret") == 200) {
                                    onSendToClientFinishSuccess(context, sendData);
                                    onParseDeepLinkIfExist(context, jsonObject);
                                } else {
                                    onSendToClientFinishFail(context, sendData, "sendDataToClient, ret is not 200");
                                }
                            } catch (Throwable e) {
                                onFailed(new Exception(e));
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            mClientSending = false;
                            onSendToClientFinishFail(context, sendData, e.getMessage());
                        }
                    };

                    try {
                        String request = mDataManager.getRequestFromSendData(context, sendData);
                        String actionApi = getDataSenderActionApi(sendData);
                        if (actionApi == null) {
                            onSendToClientFinishFail(context, sendData, "sendDataToClient, data category error");
                            return;
                        }
                        AdsforceRequestSender.sendDataToClient(actionApi, request,
                                sendData.getClientTime(), sendData.getUuid(), callback);
                    } catch (Throwable throwable) {
                        mClientSending = false;
                    }
                }
            };
            mClientSending = true;
            WorkThreadManager.runInHttpThreadPool(runnable);
        } catch (Throwable e) {
            mClientSending = false;
        }
    }

    public void sendDataToServer(final Context context) {
        try {
            if (mServerSending) {
                LogUtils.warn("sendDataToServer, data is sending", null);
                return;
            }

            final AdsforceBaseEntity sendData = mDataManager.getServerSendData(context);
            if (sendData == null) {
                LogUtils.warn("sendDataToServer, no data waiting to send", null);
                return;
            }

            if (!NetUitls.isNetworkAvailable(context)) {
                if (mServerDelayRunnable == null) {
                    mServerDelayRunnable = new Runnable() {
                        @Override
                        public void run() {
                            sendDataToServer(context);
                        }
                    };
                }
                WorkThreadManager.removeInWorkThread(mServerDelayRunnable);
                WorkThreadManager.runInWorkThread(mServerDelayRunnable, Constants.SEND_RETRY_MILLS);
                return;
            } else {
                if (mServerDelayRunnable != null) {
                    WorkThreadManager.removeInWorkThread(mServerDelayRunnable);
                    mServerDelayRunnable = null;
                }
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Callback callback = new Callback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            try {
                                mServerSending = false;
                                JSONObject jsonObject = new JSONObject(data);
                                if (jsonObject != null && jsonObject.optInt("ret") == 200) {
                                    onSendToServerFinishSuccess(context, sendData);
                                } else {
                                    onSendToClientFinishFail(context, sendData, "sendDataToServer, ret is not 200");
                                }
                            } catch (Throwable e) {
                                onFailed(new Exception(e.getMessage()));
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            mServerSending = false;
                            onSendToServerFinishFail(context, sendData, e.getMessage());
                        }
                    };

                    try {
                        String request = mDataManager.getRequestFromSendData(context, sendData);
                        String actionApi = getDataSenderActionApi(sendData);
                        if (actionApi == null) {
                            onSendToServerFinishFail(context, sendData, "sendDataToServer, data category error");
                            return;
                        }
                        AdsforceRequestSender.sendDataToServer(actionApi, request,
                                sendData.getClientTime(), sendData.getUuid(), callback);
                    } catch (Throwable throwable) {
                        mServerSending = false;
                    }
                }
            };
            mServerSending = true;
            WorkThreadManager.runInHttpThreadPool(runnable);
        } catch (Throwable e) {
            mServerSending = false;
        }
    }

    public void onSendToClientFinishSuccess(Context context, AdsforceBaseEntity sendData) {
        LogUtils.info("AdsforceDataSender onSendToClientFinishSuccess");
        try {
            mDataManager.onSendToClientSuccess(context, sendData);
        } catch (Throwable throwable) {

        }
    }

    public void onSendToClientFinishFail(final Context context, AdsforceBaseEntity sendData, final String message) {
        LogUtils.warn("AdsforceDataSender onSendToClientFinishFail, " + message, null);
        try {
            mDataManager.onSendToClientFail(context, sendData);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendDataToClient(context);
                }
            };
            WorkThreadManager.runInWorkThread(runnable, Constants.SEND_RETRY_MILLS);
        } catch (Throwable throwable) {

        }
    }

    public void onSendToServerFinishSuccess(Context context, AdsforceBaseEntity sendData) {
        LogUtils.info("AdsforceDataSender onSendToServerFinishSuccess");
        try {
            mDataManager.onSendToServerSuccess(context, sendData);
        } catch (Throwable throwable) {

        }
    }

    public void onSendToServerFinishFail(final Context context, AdsforceBaseEntity sendData, final String message) {
        LogUtils.warn("AdsforceDataSender onSendToServerFinishFail, " + message, null);
        try {
            mDataManager.onSendToServerFail(context, sendData);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendDataToServer(context);
                }
            };
            WorkThreadManager.runInWorkThread(runnable, Constants.SEND_RETRY_MILLS);
        } catch (Throwable throwable) {

        }
    }

    public void onParseDeepLinkIfExist(Context context, JSONObject jsonObject) {
        try {
            mDataManager.onParseDeepLinkIfExist(context, jsonObject);
        } catch (Throwable throwable) {

        }
    }

    private String getDataSenderActionApi(AdsforceBaseEntity eventCategory) {
        switch (eventCategory.getEventCategory()) {
            case Constants.CATEGORY_INSTALL:
                return Constants.ACTION_INSTALL;
            case Constants.CATEGORY_EVENT:
                return Constants.ACTION_EVENT;
            case Constants.CATEGORY_SESSION:
                return Constants.ACTION_SESSION;
            case Constants.CATEGORY_REVENUE:
            case Constants.CATEGORY_REVENUE_VERIFY:
                return Constants.ACTION_REVENUE;
            default:
                return null;
        }
    }

}
