package com.adsforce.sdk.sender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by t.wang on 2018/7/20.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceDataSenderFactory {
    public static Map<String, AdsforceDataSender> sCache = new ConcurrentHashMap<>();

    public static AdsforceDataSender getAdsforceDataSender(String key) {
        if (!sCache.containsKey(key)) {
            AdsforceDataSender sender = new AdsforceDataSender();
            sCache.put(key, sender);
        }

        AdsforceDataSender sender = sCache.get(key);
        return sender;
    }

    public static void removeSenderIfNeccesary(String key) {
        if (sCache.containsKey(key)) {
            sCache.remove(key);
        }
    }
}
