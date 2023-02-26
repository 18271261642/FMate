package com.app.fmate.ui.fragment.map.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 功能:这是一个避免内存泄露的Handler
 */

public class WeakHandler<T extends WeakHandler.CallBack> extends Handler {


    private WeakReference<T> wr;

    public WeakHandler(T t) {
        super(Looper.getMainLooper());
        wr = new WeakReference<T>(t);
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t = wr.get();
        if (t != null) {
            t.handleMessage(msg);
        }
    }
    public interface CallBack {
        void handleMessage(Message msg);
    }
}
