package com.app.fmate.utils;


import android.os.Handler;
import android.os.Looper;


import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 */
public class ThreadUtils {
    private static final String LOG_TAG = "线程工具类";
    private static Handler mMainLooperHandler = new Handler(Looper.getMainLooper());
    private static ScheduledThreadPoolExecutor threadPoolInstance;

    private static ScheduledThreadPoolExecutor getThreadPool() {
        return threadPoolInstance == null ? (threadPoolInstance
                = new ScheduledThreadPoolExecutor(
                Math.max(8, Runtime.getRuntime().availableProcessors() * 2)
                , threadFactory("manniuInPoolThread-", false)
        )) : threadPoolInstance;
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(() -> {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        //ToastUtils.showShort("线程池中的某个线程发生了问题，请查看控制台或者日志文件！。");
                        //DefaultExceptionHandler.getInstance().uncaughtException(Thread.currentThread(), e);
                    }
                }, name + System.currentTimeMillis());
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    /**
     * 从线程池中创建子线程执行异步任务
     * 在任务数超过最大值，或者线程池Shutdown时将抛出异常
     *
     * @param runnable Runnable
     */
    public static Future<?> submit(Runnable runnable) {
        if (getThreadPool().getQueue().size() == getThreadPool().getMaximumPoolSize() || getThreadPool().isShutdown()) {
//            LogUtils.e(LOG_TAG + "线程池爆满警告，请查看是否开启了过多的耗时线程"
//                    + new RuntimeException("线程池爆满警告，请查看是否开启了过多的耗时线程"));
            //重置一下线程池，并且抛弃之前的线程池的引用（TODO 优化此处的逻辑，要在App退出时销毁线程池的！）
            threadPoolInstance = null;
        }
        return getThreadPool().submit(runnable);
    }

    /**
     * 从线程池中创建子线程执行异步任务
     * 在任务数超过最大值，或者线程池Shutdown时将抛出异常
     * 参数列表详见
     * {@link ScheduledThreadPoolExecutor#schedule(Runnable, long, TimeUnit)}
     */
    public static Future<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (getThreadPool().getQueue().size() == getThreadPool().getMaximumPoolSize() || getThreadPool().isShutdown()) {
//            LogUtil.e(LOG_TAG + "线程池爆满警告，请查看是否开启了过多的耗时线程"
//                    , new RuntimeException("线程池爆满警告，请查看是否开启了过多的耗时线程"));
            //重置一下线程池，并且抛弃之前的线程池的引用（TODO 优化此处的逻辑，要在App退出时销毁线程池的！）
            threadPoolInstance = null;
        }
        return getThreadPool().schedule(runnable, delay, unit);

    }

    public void releaseThreadPool() {
        getThreadPool().shutdown();
    }

    /**
     * 延时切换到主线程
     *
     * @param runnable Runnable
     * @param delayed  时长 Millis
     */
    public static void runOnUiThread(Runnable runnable, long delayed) {
        mMainLooperHandler.postDelayed(runnable, delayed);
    }

    /**
     * 切换到主线程
     *
     * @param runnable Runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        mMainLooperHandler.post(runnable);
    }

    /**
     * 切换到主线程并尽可能立刻执行。
     *
     * @param runnable Runnable
     */
    public static void runOnUiThreadImediatly(Runnable runnable) {
        mMainLooperHandler.postAtFrontOfQueue(runnable);
    }

    /**
     * 切换到主线程
     *
     * @param runnable Runnable
     */
    public static void runOnUiThreadDelay(Runnable runnable, long delayMillis) {
        mMainLooperHandler.postDelayed(runnable, delayMillis);
    }
}
