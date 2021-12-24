package com.shon.net.util;

import android.util.Log;

/**
 * Date : 2020/7/21 11:20
 * Package name : net.yt.whale.net.util
 * Des :
 */
public final class RetrofitLog {
    private RetrofitLog() {

    }

    // 标签
    private static String TAG = "RetrofitLog";

    //是否显示详细信息
    private static boolean needInfo = true;

    public static void setTAG(String TAG) {
        RetrofitLog.TAG = TAG;
    }

    public static void setNeedInfo(boolean needInfo) {
        RetrofitLog.needInfo = needInfo;
    }

    /**
     * @param msg
     */
    public static void e(String msg) {
        Log.e(TAG, getFormatString(msg) + getCurrentInfo());
    }

    /**
     * @param msg
     */
    public static void d(String msg) {
        Log.d(TAG, getFormatString(msg) + getCurrentInfo());
    }

    /**
     * @param msg
     */
    public static void i(String msg) {
        Log.i(TAG, getFormatString(msg) + getCurrentInfo());
    }

    /**
     * @param msg
     */
    public static void w(String msg) {
        Log.w(TAG, getFormatString(msg) + getCurrentInfo());
    }

    /**
     * @param msg
     *
     * @return
     */
    private static String getFormatString(String msg) {
        return "{ " + msg + " }";
    }

    /**
     * 获取当前的信息
     */
    private static String getCurrentInfo() {
        if (!needInfo) {
            return "";
        }
        // int index = 2;
        // StackTraceElement[] eles = new Throwable().getStackTrace();
        // 位置不对，自己修改
        int index = 4;
        //
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
        if (eles.length < (index + 1)) {
            return "Unknown class info !!!";
        }
        StackTraceElement e = eles[index];

        return "\n" + "FileName : " + e.getFileName() + "||" +
                "ClassName : " + e.getClassName() + "\n" +
                "MethodName : " + e.getMethodName() + "||" +
                "LineNumber : " + e.getLineNumber() + "\n";
    }
}
