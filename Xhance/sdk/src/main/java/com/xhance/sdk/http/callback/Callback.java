package com.xhance.sdk.http.callback;

public interface Callback<T> {
    void onSuccess(T data);

    void onFailed(Exception e);
}
