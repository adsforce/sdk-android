package com.xhance.sdk.sender;

import android.content.Context;

import com.xhance.sdk.http.callback.Callback;
import com.xhance.sdk.manager.WorkThreadManager;
import com.xhance.sdk.manager.XhanceDataManager;
import com.xhance.sdk.utils.Constants;
import com.xhance.sdk.utils.LogUtils;
import com.xhance.sdk.utils.NetUitls;

import org.json.JSONObject;

public class XhanceDataSender {
    private XhanceDataManager mDataManager;
    private boolean mClientSending;
    private Runnable mClientDelayRunnable;
    private boolean mServerSending;
    private Runnable mServerDelayRunnable;

    public void setDataManager(XhanceDataManager dataManager) {
        mDataManager = dataManager;
    }

    public void sendDataToClient(final Context context) {
        try {
            if (mClientSending) {
                LogUtils.warn("sendDataToClient has data sending", null);
                return;
            }

            final XhanceBaseEntity sendData = mDataManager.getClientSendData(context);
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
                                    onSendToClientFinishFail(context, sendData, "ret is not 200");
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
                        XhanceRequestSender.sendDataToClient(mDataManager.getActionApiName(),
                                request, sendData.getClientTime(), sendData.getUuid(), callback);
                    } catch (Throwable throwable) {

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
                LogUtils.warn("sendDataToServer has data sending", null);
                return;
            }

            final XhanceBaseEntity sendData = mDataManager.getServerSendData(context);
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
                                    onSendToClientFinishFail(context, sendData, "ret is not 200");
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
                        XhanceRequestSender.sendDataToServer(mDataManager.getActionApiName(),
                                request, sendData.getClientTime(), sendData.getUuid(), callback);
                    } catch (Throwable throwable) {

                    }
                }
            };
            mServerSending = true;
            WorkThreadManager.runInHttpThreadPool(runnable);
        } catch (Throwable e) {
            mServerSending = false;
        }
    }

    public void onSendToClientFinishSuccess(Context context, XhanceBaseEntity sendData) {
        LogUtils.info("XhanceDataSender onSendToClientFinishSuccess");
        try {
            mDataManager.onSendToClientSuccess(context, sendData);
        } catch (Throwable throwable) {

        }
    }

    public void onSendToClientFinishFail(final Context context, XhanceBaseEntity sendData, final String message) {
        LogUtils.warn("XhanceDataSender onSendToClientFinishFail, " + message, null);
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

    public void onSendToServerFinishSuccess(Context context, XhanceBaseEntity sendData) {
        LogUtils.info("XhanceDataSender onSendToServerFinishSuccess");
        try {
            mDataManager.onSendToServerSuccess(context, sendData);
        } catch (Throwable throwable) {

        }
    }

    public void onSendToServerFinishFail(final Context context, XhanceBaseEntity sendData, final String message) {
        LogUtils.warn("XhanceDataSender onSendToServerFinishFail, " + message, null);
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
}
