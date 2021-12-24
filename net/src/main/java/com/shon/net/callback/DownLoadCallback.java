package com.shon.net.callback;

/**
 * Date : 2020/7/21 15:01
 * Package name : net.yt.whale.net.callback
 * Des : 下载回调
 * <p>
 * run on main thread
 */
public interface DownLoadCallback {
    void onDownLoadStart(String fileName);

    void onDownLoading(long totalSize, long currentSize, int progress);

    void onDownLoadSuccess();

    void onDownLoadError();
}
