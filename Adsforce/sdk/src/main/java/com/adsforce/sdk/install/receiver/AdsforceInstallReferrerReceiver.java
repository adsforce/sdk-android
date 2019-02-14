package com.adsforce.sdk.install.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adsforce.sdk.AdsforceSdk;
import com.adsforce.sdk.install.AdsforceInstallManager;
import com.adsforce.sdk.utils.LogUtils;
import com.adsforce.sdk.utils.Utils;

import static com.adsforce.sdk.utils.Constants.INSTALL_REFERRER_CLIENT_CLASS;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */
public class AdsforceInstallReferrerReceiver extends BroadcastReceiver {
    private static final String PARAM_REFERRER = "referrer";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.hasClass(INSTALL_REFERRER_CLIENT_CLASS)) {
            LogUtils.warn("AdsforceInstallReferrerReceiver has GoogleInstallReferrer API", null);
            return;
        }

        String referrer = "";
        if (intent.hasExtra(PARAM_REFERRER)) {
            referrer = intent.getExtras().getString(PARAM_REFERRER);
        }

        LogUtils.info("AdsforceInstallReferrerReceiver received referrer is " + referrer);
        referrer = Utils.getEncodeReferrer(referrer);
        AdsforceSdk.setContext(context.getApplicationContext());
        AdsforceInstallManager.getInstance().handleInstallReferrer(context.getApplicationContext(),
                referrer, System.currentTimeMillis(), 0, 0);
    }

}
