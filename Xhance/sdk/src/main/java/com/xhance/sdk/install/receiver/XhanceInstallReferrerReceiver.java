package com.xhance.sdk.install.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xhance.sdk.XhanceSdk;
import com.xhance.sdk.install.XhanceInstallManager;
import com.xhance.sdk.utils.LogUtils;
import com.xhance.sdk.utils.Utils;

import static com.xhance.sdk.utils.Constants.INSTALL_REFERRER_CLIENT_CLASS;

public class XhanceInstallReferrerReceiver extends BroadcastReceiver {
    private static final String PARAM_REFERRER = "referrer";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.hasClass(INSTALL_REFERRER_CLIENT_CLASS)) {
            LogUtils.warn("XhanceSdkInstallReferrerReceiver has GoogleInstallReferrer API", null);
            return;
        }

        String referrer = "";
        if (intent.hasExtra(PARAM_REFERRER)) {
            referrer = intent.getExtras().getString(PARAM_REFERRER);
        }

        LogUtils.info("XhanceSdkInstallReferrerReceiver received referrer is " + referrer);
        referrer = Utils.getEncodeReferrer(referrer);
        XhanceSdk.setContext(context.getApplicationContext());
        XhanceInstallManager.getInstance().handleInstallReferrer(context.getApplicationContext(),
                referrer, System.currentTimeMillis(), 0, 0);
    }

}
