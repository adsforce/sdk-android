package com.adsforce.sdk.http.callback;

/**
 * Created by t.wang on 2018/4/13.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public interface Callback<T> {
    void onSuccess(T data);

    void onFailed(Exception e);
}
