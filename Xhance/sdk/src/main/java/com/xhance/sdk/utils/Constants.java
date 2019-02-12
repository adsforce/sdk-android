package com.xhance.sdk.utils;

import android.text.format.DateUtils;

public class Constants {
    public static final String SERVER_URL_TEST = "https://track-digest-test.xhance.io/";
    public static final String SERVER_URL = "https://digest-track.xhance.io/";

    public static final String INSTALL_REFERRER_CLIENT_CLASS = "com.android.installreferrer.api.InstallReferrerClient";
    public static final String ENCODE_UTF8 = "UTF-8";

    public static final long SESSION_OUT_TIME_MILLS = 30 * DateUtils.MINUTE_IN_MILLIS;
    public static final long FIVE_MINUTES = 5 * DateUtils.MINUTE_IN_MILLIS;
    public static final long SEND_RETRY_MILLS = 10 * DateUtils.SECOND_IN_MILLIS;
    public static final long SEND_MAX_COUNTS = 5;

    public static final String SP_SETTING_FILE_NAME = "xhance_setting_file";
    public static final String KEY_SETTING_OPEN_ID = "open_id";
    public static final String KEY_SETTING_ANDROID_ID = "android_id";
    public static final String KEY_SETTING_GAID = "gaid";

    public static final String SP_INSTALL_FILE_NAME = "xhance_install_file";
    public static final String KEY_INSTALL_REFERRER_SAVED = "install_referrer_saved";
    public static final String KEY_INSTALL_CLIENT_DATA = "client_data";
    public static final String KEY_INSTALL_SERVER_DATA = "server_data";
    public static final String KEY_INSTALL_REFERRER = "install_referrer";
    public static final String KEY_INSTALL_CLIENT_TIME = "client_time";
    public static final String KEY_INSTALL_REFFER_CLICK_TIME = "reffer_click_time";
    public static final String KEY_INSTALL_INSTALL_CLICK_TIME = "install_click_time";
    public static final String KEY_INSTALL_DATA_SENDED = "data_sended";
    public static final String KEY_INSTALL_SEND_COUNT = "send_count";
    public static final String KEY_UUID = "uuid";

    public static final String SP_DEEPLINK_FILE_NAME = "xhance_deeplink_file";
    public static final String KEY_DEEPLINK_STATUS = "deeplink_status";
    public static final String KEY_DEEPLINK_URI = "deeplink_uri";
    public static final String KEY_DEEPLINK_ARGS = "deeplink_params";

    public static final String SP_EVENT_FILE_NAME = "xhance_event_file";
    public static final String KEY_EVNET_LAST_ACTIVE_TIME = "last_active_time";
    public static final String KEY_EVENT_LAST_SESSION_ID = "last_session_id";
    public static final String KEY_EVENT_LAST_SESSION_START_TIME = "last_session_start_time";
    public static final String KEY_EVENT_LAST_SESSION_END_TIME = "last_session_end_time";
    public static final String KEY_EVENT_DATA_CLIENT_ARRAY = "data_client_array";
    public static final String KEY_EVENT_DATA_SERVER_ARRAY = "data_server_array";
    public static final String KEY_EVENT_DATA_ID = "data_id";
    public static final String KEY_EVENT_SESSION_ID = "session_id";
    public static final String KEY_EVENT_CLIENT_TIME = "client_time";
    public static final String KEY_EVENT_TYPE = "event_type";
    public static final String KEY_EVENT_VALUE = "event_value";
    public static final String KEY_EVENT_CATEGORY = "event_category";
    public static final String KEY_EVENT_DATA_SENDED = "data_sended";
    public static final String KEY_EVENT_SEND_COUNT = "send_count";

    public static final String KEY_REVENUE_PRICE = "revn_price";
    public static final String KEY_REVENUE_CURR = "revn_curr";
    public static final String KEY_REVENUE_ITEM_ID = "item_id";
    public static final String KEY_REVENUE_ITEM_CAT = "item_cat";
    public static final String KEY_REVENUE_PUB_KEY = "pub_key";
    public static final String KEY_REVENUE_SIGN = "revn_sign";
    public static final String KEY_REVENUE_PURCHASE_DATA = "purchase_date";
    public static final String KEY_REVENUE_PARAMS = "revn_params";

    public static final int DEEPLINK_STATE_WAITING = 0;
    public static final int DEEPLINK_STATE_RETURNED = 1;
    public static final int DEEPLINK_STATE_FETCHED = 2;

    public static final String ACTION_INSTALL = "install";
    public static final String ACTION_EVENT = "event";

    public static final String CATEGORY_SESSION = "session";
    public static final String CATEGORY_REVENUE = "revenue";
    public static final String CATEGORY_EVENT = "event";
    public static final String CATEGORY_REVENUE_VERIFY = "revenue_verify";
}
