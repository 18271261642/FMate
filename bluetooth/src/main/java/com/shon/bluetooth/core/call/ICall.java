package com.shon.bluetooth.core.call;

import androidx.annotation.NonNull;

/**
 * Auth : frank
 * Date : 2020/10/05 19:23
 * Package name : com.shon.bluetooth.contorller.imp
 * Des :
 */
public interface ICall<T> {
    @NonNull
    String getAddress();

    @NonNull
    T getCallBack();

    boolean isPriority();

    void enqueue(@NonNull T iCallback);

    void cancel();
}
