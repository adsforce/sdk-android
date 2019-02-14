package com.adsforce.sdk.install;

import android.content.Context;
import android.text.TextUtils;

import com.adsforce.sdk.sender.AdsforceDataSender;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.adsforce.sdk.AdsforceSdk;
import com.adsforce.sdk.config.AdsforceSdkConfig;
import com.adsforce.sdk.deeplink.AdsforceDeepLinkManager;
import com.adsforce.sdk.manager.SharedPreferencesManager;
import com.adsforce.sdk.manager.WorkThreadManager;
import com.adsforce.sdk.manager.AdsforceDataManager;
import com.adsforce.sdk.sender.AdsforceDataSenderFactory;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.DeviceInfo;
import com.adsforce.sdk.utils.LogUtils;
import com.adsforce.sdk.utils.Utils;

import org.json.JSONObject;

import java.util.SortedMap;

import static com.adsforce.sdk.utils.Constants.INSTALL_REFERRER_CLIENT_CLASS;
import static com.adsforce.sdk.utils.Constants.KEY_INSTALL_CLIENT_DATA;
import static com.adsforce.sdk.utils.Constants.KEY_INSTALL_SERVER_DATA;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceInstallManager implements AdsforceDataManager<AdsforceInstallEntity> {
    private static AdsforceInstallManager sInstance;
    private AdsforceDataSender mInstallSender;
    private SharedPreferencesManager mSpManager;

    private Context mContext;
    private boolean mClientDataSended;
    private boolean mServerDataSended;

    public static AdsforceInstallManager getInstance() {
        if (sInstance == null) {
            synchronized (AdsforceInstallManager.class) {
                if (sInstance == null) {
                    sInstance = new AdsforceInstallManager();
                }
            }
        }

        return sInstance;
    }

    public void init(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (mSpManager == null) {
            mSpManager = new SharedPreferencesManager(Constants.SP_INSTALL_FILE_NAME);
        }
        if (mInstallSender == null) {
            mInstallSender = AdsforceDataSenderFactory.getAdsforceDataSender(getDataSenderName());
            mInstallSender.setDataManager(this);
        }
        if (Utils.hasClass(INSTALL_REFERRER_CLIENT_CLASS)) {
            if (mSpManager.getBoolean(context, Constants.KEY_INSTALL_REFERRER_SAVED)) {
                LogUtils.warn("AdsforceInstallManager install referrer has been saved ......", null);
            } else {
                getInstallReferrerFromGoogleApi(context);
            }
        }

        checkInstallDataWhenAppLaunched(context);
    }

    private void getInstallReferrerFromGoogleApi(final Context context) {
        final InstallReferrerClient mReferrerClient = InstallReferrerClient.newBuilder(context).build();
        LogUtils.info("GoogleInstallReferrer startConnection ... ...");
        try {
            mReferrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    switch (responseCode) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            try {
                                ReferrerDetails response = mReferrerClient.getInstallReferrer();
                                String referrer = response.getInstallReferrer();
                                long rts = response.getReferrerClickTimestampSeconds();
                                long gts = response.getInstallBeginTimestampSeconds();
                                mReferrerClient.endConnection();

                                LogUtils.info("install referrer is " + referrer);
                                handleInstallReferrer(context.getApplicationContext(), Utils.getEncodeReferrer(referrer), System.currentTimeMillis(), rts, gts);
                            } catch (Throwable e) {

                            }
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            LogUtils.warn("GoogleInstallReferrer not supported", null);
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            LogUtils.warn("GoogleInstallReferrer unable to connect to the service", null);
                            break;
                        default:
                            LogUtils.warn("GoogleInstallReferrer responseCode not found", null);
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    LogUtils.info("GoogleInstallReferrer onInstallReferrerServiceDisconnected");
                }
            });
        } catch (Throwable throwable) {
        }
    }

    private void checkInstallDataWhenAppLaunched(Context context) {
        try {
            String installData = mSpManager.getString(context, KEY_INSTALL_CLIENT_DATA);
            if (!TextUtils.isEmpty(installData)) {
                JSONObject jsonObject = new JSONObject(installData);
                AdsforceInstallEntity installEntity = new AdsforceInstallEntity(jsonObject);
                if (installEntity != null && !installEntity.isSended()) {
                    installEntity.setSendCount(0);
                    saveAndSendClientData(context, installEntity);
                } else {
                    mClientDataSended = true;
                }
            }

            installData = mSpManager.getString(context, KEY_INSTALL_SERVER_DATA);
            if (!TextUtils.isEmpty(installData)) {
                JSONObject jsonObject = new JSONObject(installData);
                AdsforceInstallEntity installEntity = new AdsforceInstallEntity(jsonObject);
                if (installEntity != null && !installEntity.isSended()) {
                    installEntity.setSendCount(0);
                    saveAndSendServerData(context, installEntity);
                } else {
                    mServerDataSended = true;
                }
            }
        } catch (Throwable throwable) {

        } finally {
            recoveryMemoryIfNeccesary();
        }
    }

    public void handleInstallReferrer(final Context context, final String referrer,
                                      final long cts, final long rts, final long gts) {
        if (mContext == null || mSpManager == null) {
            init(context.getApplicationContext());
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSpManager.getBoolean(context, Constants.KEY_INSTALL_REFERRER_SAVED)) {
                        LogUtils.warn("AdsforceInstallManager install referrer has been saved ......", null);
                        return;
                    }
                    mSpManager.putBoolean(context, Constants.KEY_INSTALL_REFERRER_SAVED, true);
                    saveAndSendInstallReferrer(context, referrer, cts, rts, gts);
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    private boolean saveAndSendInstallReferrer(Context context, String referrer, long cts, long rts, long gts) {
        try {
            AdsforceInstallEntity installEntity = new AdsforceInstallEntity();
            installEntity.setReffer(referrer);
            installEntity.setClientTime(cts);
            installEntity.setReferrerClickTime(rts);
            installEntity.setInstallClickTime(gts);
            installEntity.setUuid(Utils.getMd5Uuid());

            saveAndSendClientData(context, installEntity);
            saveAndSendServerData(context, installEntity);
        } catch (Throwable throwable) {
            LogUtils.warn(
                    "AdsforceInstallManager saveAndSendInstallReferrer failed, " + throwable.getMessage(), throwable);
            return false;
        }

        return true;
    }

    private void saveAndSendClientData(Context context, AdsforceInstallEntity installEntity) throws Throwable {
        JSONObject jsonObject = installEntity.convertToJson();
        mSpManager.putString(mContext, KEY_INSTALL_CLIENT_DATA, jsonObject.toString());
        if (installEntity != null && !installEntity.isSended()) {
            mInstallSender.sendDataToClient(context);
        }
    }

    private void saveAndSendServerData(Context context, AdsforceInstallEntity installEntity) throws Throwable {
        JSONObject jsonObject = installEntity.convertToJson();
        mSpManager.putString(mContext, KEY_INSTALL_SERVER_DATA, jsonObject.toString());
        if (installEntity != null && !installEntity.isSended()) {
            mInstallSender.sendDataToServer(context);
        }
    }

    public AdsforceInstallEntity getClientSendData(Context context) {
        try {
            String installData = mSpManager.getString(context, KEY_INSTALL_CLIENT_DATA);
            if (TextUtils.isEmpty(installData)) {
                LogUtils.warn("install_client_data is empty", null);
                return null;
            } else {
                LogUtils.debug("install_client_data is " + installData);
            }

            JSONObject jsonObject = new JSONObject(installData);
            AdsforceInstallEntity installEntity = new AdsforceInstallEntity(jsonObject);
            if (installEntity.getSendCount() >= Constants.SEND_MAX_COUNTS) {
                LogUtils.warn("install_client_data, send too much", null);
                return null;
            }
            return installEntity;
        } catch (Throwable throwable) {

        }

        return null;
    }

    public AdsforceInstallEntity getServerSendData(Context context) {
        try {
            String installData = mSpManager.getString(context, KEY_INSTALL_SERVER_DATA);
            if (TextUtils.isEmpty(installData)) {
                LogUtils.warn("install_server_data is empty", null);
                return null;
            } else {
                LogUtils.debug("install_server_data is " + installData);
            }

            JSONObject jsonObject = new JSONObject(installData);
            AdsforceInstallEntity installEntity = new AdsforceInstallEntity(jsonObject);
            if (installEntity.getSendCount() >= Constants.SEND_MAX_COUNTS) {
                LogUtils.warn("install_server_data, send too much", null);
                return null;
            }
            return installEntity;
        } catch (Throwable throwable) {

        }

        return null;
    }

    public void onSendToClientSuccess(final Context context, final AdsforceInstallEntity installData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    installData.setSended(true);
                    mSpManager.putString(context, Constants.KEY_INSTALL_CLIENT_DATA, installData.convertToJson().toString());
                    mClientDataSended = true;
                    recoveryMemoryIfNeccesary();
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onSendToClientFail(final Context context, final AdsforceInstallEntity installData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    int count = installData.getSendCount();
                    installData.setSendCount(count + 1);
                    mSpManager.putString(context, Constants.KEY_INSTALL_CLIENT_DATA, installData.convertToJson().toString());
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onSendToServerSuccess(final Context context, final AdsforceInstallEntity installData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    installData.setSended(true);
                    mSpManager.putString(context, Constants.KEY_INSTALL_SERVER_DATA, installData.convertToJson().toString());
                    mServerDataSended = true;
                    recoveryMemoryIfNeccesary();
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onSendToServerFail(final Context context, final AdsforceInstallEntity installData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    int count = installData.getSendCount();
                    installData.setSendCount(count + 1);
                    mSpManager.putString(context, Constants.KEY_INSTALL_SERVER_DATA, installData.convertToJson().toString());
                } catch (Throwable throwable) {

                }
            }
        };
        WorkThreadManager.runInWorkThread(runnable);
    }

    public void onParseDeepLinkIfExist(Context context, JSONObject jsonObject) {
        AdsforceDeepLinkManager.getInstance().parseDeepLink(context, jsonObject);
    }

    public String getDataSenderName() {
        return Constants.ACTION_INSTALL;
    }

    public String getRequestFromSendData(Context context, AdsforceInstallEntity installEntity) {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        AdsforceSdkConfig config = AdsforceSdk.getSdkConfig();
        SortedMap<String, String> params = deviceInfo.getDeviceInfoParams(context);
        params.put("referrer", installEntity.getReffer());
        params.put("devkey", config.getDevKey());
        params.put("cid", config.getChannelId());
        params.put("cts", installEntity.getClientTime() + "");
        params.put("ref_click_ts", installEntity.getReferrerClickTime() + "");
        params.put("gp_click_ts", installEntity.getInstallClickTime() + "");
        params.put("ladt", deviceInfo.getLadt(context) + "");
        params.put("hafb", deviceInfo.hasFB(context) ? "1" : "0");
        params.put("hagp", deviceInfo.hasGP(context) ? "1" : "0");
        params.put("hags", deviceInfo.hasGooleService(context) + "");

        String request = Utils.convertMapToString(params);
        LogUtils.info("AdsforceInstallManager install request params is \n" + request);
        return request;
    }

    private void recoveryMemoryIfNeccesary() {
        if (mClientDataSended && mServerDataSended) {
            AdsforceDataSenderFactory.removeSenderIfNeccesary(getDataSenderName());
            mInstallSender = null;
            mSpManager = null;
            sInstance = null;
        }
    }

}
