package com.app.fmate.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 功能:计时器
 */

public class CountTimer {

    private Handler mHandler;
    private long delayedTime;
    private long intervalTime;
    private int HANDLER_COUNT_TIMER = 0;
    private long millisecond;

    public CountTimer(final long intervalTime, final OnCountTimerListener timerListener) {
        init(-1, intervalTime, timerListener);
    }

    public CountTimer(long delayedTime, final long intervalTime, final OnCountTimerListener timerListener) {
        init(delayedTime, intervalTime, timerListener);
    }

    private void init(long delayedTime, final long intervalTime, final OnCountTimerListener timerListener) {
        this.delayedTime = delayedTime;
        this.intervalTime = intervalTime;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                millisecond += intervalTime;
                mHandler.removeMessages(HANDLER_COUNT_TIMER);
                mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_TIMER, CountTimer.this.intervalTime);

                if (timerListener != null) {
                    if (CountTimer.this.delayedTime != -1) {
                        timerListener.onCountTimerChanged(CountTimer.this.delayedTime + millisecond);
                    } else {
                        timerListener.onCountTimerChanged(millisecond);
                    }
                }
            }
        };
    }

    public void start() {
        millisecond = 0;
        mHandler.removeMessages(HANDLER_COUNT_TIMER);
        if (delayedTime == -1) {
            mHandler.sendEmptyMessage(HANDLER_COUNT_TIMER);
        } else {
            mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_TIMER,delayedTime);
        }
    }

    public void stop() {
        mHandler.removeMessages(HANDLER_COUNT_TIMER);
    }

    public interface OnCountTimerListener {
        void onCountTimerChanged(long millisecond);
    }
}
