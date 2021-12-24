package com.shon.net.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Date : 2020/7/29 10:38
 * Package name : net.yt.whale.net.util
 * Des : 运行线程切换工具
 */
public class NetExecutors {

    // io 线程
    private final Executor mDiskIO;

    //网络线程
    private final Executor mNetworkIO;

    //main tread
    private final Executor mMainThread;

    private static NetExecutors appExecutors;

    //单例
    public static NetExecutors getInstance() {
        if (appExecutors == null) {
            synchronized (NetExecutors.class) {
                if (appExecutors == null) {
                    appExecutors = new NetExecutors();
                }
            }
        }
        return appExecutors;
    }

    private NetExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
    }

    private NetExecutors() {
        this(Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(3),
                new MainThreadExecutor());
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler;

        private MainThreadExecutor(){
            mainThreadHandler = new Handler(Looper.getMainLooper());
        }


        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
