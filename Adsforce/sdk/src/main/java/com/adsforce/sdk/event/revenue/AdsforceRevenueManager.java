package com.adsforce.sdk.event.revenue;

import android.content.Context;

import com.adsforce.sdk.event.AdsforceEventEntity;
import com.adsforce.sdk.event.AdsforceEventManager;
import com.adsforce.sdk.utils.Constants;
import com.adsforce.sdk.utils.Utils;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by t.wang on 2018/5/29.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceRevenueManager {
    private static AdsforceRevenueManager sAdsforceRevenueManager;
    private AdsforceEventManager mEventManager;

    public static AdsforceRevenueManager getInstance() {
        if (sAdsforceRevenueManager == null) {
            synchronized (AdsforceRevenueManager.class) {
                if (sAdsforceRevenueManager == null) {
                    sAdsforceRevenueManager = new AdsforceRevenueManager();
                }
            }
        }

        return sAdsforceRevenueManager;
    }

    public void init(Context context) {
        if (mEventManager == null) {
            mEventManager = AdsforceEventManager.getInstance();
            mEventManager.init(context);
        }
    }

    public void logRevenue(String price, String currency, String productId, String productType) {
        try {
            AdsforceEventEntity data = new AdsforceEventEntity();
            data.setEventCategory(Constants.CATEGORY_REVENUE);
            data.setClientTime(System.currentTimeMillis());
            data.setRevnPrice(price);
            data.setRevnCurr(currency);
            data.setRevnItemId(productId);
            data.setRevnItemCat(productType);
            mEventManager.saveAndSendEventDataArray(data);
        } catch (Throwable throwable) {

        }
    }

    public void logRevenueVerify(String price, String currency, String pubKey, String dataSignature,
                                 String purchaseData, Map<String, String> params) {
        try {
            AdsforceEventEntity data = new AdsforceEventEntity();
            data.setEventCategory(Constants.CATEGORY_REVENUE_VERIFY);
            data.setClientTime(System.currentTimeMillis());
            data.setRevnPrice(price);
            data.setRevnCurr(currency);
            data.setRevnPubKey(pubKey);
            data.setRevnSign(dataSignature);
            data.setRevnPurchaseData(URLEncoder.encode(purchaseData, Constants.ENCODE_UTF8));
            String paramString = Utils.convertMapToJson(params);
            data.setRevnParams(paramString);
            mEventManager.saveAndSendEventDataArray(data);
        } catch (Throwable throwable) {

        }
    }
}
