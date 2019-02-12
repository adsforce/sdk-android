package com.xhance.sdk.event.revenue;

import android.content.Context;

import com.xhance.sdk.event.XhanceEventEntity;
import com.xhance.sdk.event.XhanceEventManager;
import com.xhance.sdk.utils.Constants;
import com.xhance.sdk.utils.Utils;

import java.net.URLEncoder;
import java.util.Map;

public class XhanceRevenueManager {
    private static XhanceRevenueManager sXhanceRevenueManager;
    private XhanceEventManager mEventManager;

    public static XhanceRevenueManager getInstance() {
        if (sXhanceRevenueManager == null) {
            synchronized (XhanceRevenueManager.class) {
                if (sXhanceRevenueManager == null) {
                    sXhanceRevenueManager = new XhanceRevenueManager();
                }
            }
        }

        return sXhanceRevenueManager;
    }

    public void init(Context context) {
        if (mEventManager == null) {
            mEventManager = XhanceEventManager.getInstance();
            mEventManager.init(context);
        }
    }

    public void logRevenue(String price, String currency, String productId, String productType) {
        try {
            XhanceEventEntity data = new XhanceEventEntity();
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
            XhanceEventEntity data = new XhanceEventEntity();
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
