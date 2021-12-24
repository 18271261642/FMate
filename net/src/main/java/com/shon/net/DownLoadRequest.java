package com.shon.net;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.shon.net.callback.DownLoadCallback;
import com.shon.net.interceptor.TokenAddInterceptor;
import com.shon.net.util.NetExecutors;
import com.shon.net.util.RetrofitLog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Date : 2020/7/21 14:24
 * Package name : net.yt.whale.net
 * Des : 下载 请求, 最终类，不允许被继承和复写
 * 重要说明：
 * 一个下载任务，对应一个 DownLoadRequest
 * 多个下载任务，对应多个 DownLoadRequest
 * <p>
 * 目标，统一管理下载任务
 * <p>
 * 本类使用方式：
 * DownLoadRequest downLoadRequest = new DownLoadRequest(fileUrl);
 * downLoadRequest.startDownLoad(saveFullPath,callback);
 * <p>
 * <p>
 * Callback 参考 {@link DownLoadCallback}
 * <p>
 * 此外， 回调是主线程执行
 */
public class DownLoadRequest extends BaseApi<DownLoadApi> {

    private int progress = -1;
    private String currentBaseUrl;
    private OkHttpClient.Builder builder;
    private DownLoadApi downLoadApi;
    private String token;

    /**
     * 根据下载地址 初始化 DownLoadRequest
     * 一个 DownLoadRequest 对应一个现在任务
     *
     * @param downLoadUrl 下载全地址
     */
    public DownLoadRequest(String downLoadUrl) {
        this("", downLoadUrl);
    }

    public DownLoadRequest(String token, String downLoadUrl) {
        super("");
        this.currentBaseUrl = downLoadUrl;
        this.token = token;

        mRetrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(getDownLoadBaseUrl())
                .build();
    }

    /**
     * 此处需要自己实现 OkHttpClient， 所以重写此方法。
     * 如无需要，请勿重写此方法
     *
     * @return OkHttpClient
     */
    @Override
    public OkHttpClient getOkHttpClient() {
        if (builder == null) {
            builder = OkHttpClientBuild.getBaseBuild();
        }
        if (!TextUtils.isEmpty(token)) {
            builder.addInterceptor(new TokenAddInterceptor(token));         //设置Token拦截器
        }
        return builder.build();
    }

    /**
     * 此处需要重写。如无需要，请勿重写
     *
     * @return DownLoadApi
     */
    @Override
    public synchronized DownLoadApi getApiInterface() {
        //重写父类方法，自己实现
        if (downLoadApi == null) {
            downLoadApi = mRetrofit.create(DownLoadApi.class);
        }
        return downLoadApi;
    }


    /**
     * 从下载地址中 获取 baseUrl
     *
     * @return baseUrl
     */
    private String getDownLoadBaseUrl() {
        HttpUrl httpUrl = HttpUrl.parse(currentBaseUrl);
        if (httpUrl == null) {
            return "";
        }
        String host = httpUrl.host();
        String baseUrl;
        if (httpUrl.isHttps()) {
            baseUrl = "https://" + host;
        } else {
            baseUrl = "http://" + host;
        }
        RetrofitLog.d("baseUrl : " + baseUrl);
        return baseUrl;
    }


    /**
     * 开始下载文件
     *
     * @param targetFile       本地存储地址，文件由使用者创建
     * @param downLoadCallback 下载过程回调
     */
    public void startDownLoad(final String targetFile, @NonNull final DownLoadCallback downLoadCallback) {

        DownLoadApi downLoadApi = getApiInterface();
        Call<ResponseBody> requestBodyCall = downLoadApi.downloadFileWithDynamicUrlSync(currentBaseUrl);
        requestBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull final Response<ResponseBody> response) {
                onHttpResponse(response, targetFile, downLoadCallback);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                RetrofitLog.e("onFailure call : " + call.toString());
                RetrofitLog.e("t : " + t.toString());
                //回调到主线程
                NetExecutors.getInstance().mainThread().execute(downLoadCallback::onDownLoadError);
            }
        });
    }

    /**
     * 下载请求结果处理
     *
     * @param response         response
     * @param targetFile       targetFile
     * @param downLoadCallback downLoadCallback
     */
    private void onHttpResponse(Response<ResponseBody> response,
                                final String targetFile, @NonNull final DownLoadCallback downLoadCallback) {
        RetrofitLog.e("response : " + response.toString());
        if (!response.isSuccessful()) {
            //下载文件失败
            NetExecutors.getInstance().mainThread().execute(downLoadCallback::onDownLoadError);
            return;
        }

        final ResponseBody responseBody = response.body();
        if (responseBody == null) {
            NetExecutors.getInstance().mainThread().execute(downLoadCallback::onDownLoadError);
            return;
        }
        //回调开始下载
        NetExecutors.getInstance().mainThread().execute(() -> downLoadCallback.onDownLoadStart(targetFile));
        // 开始写入文件
        NetExecutors.getInstance().diskIO().execute(() -> writeResponseBodyToDisk(responseBody, targetFile, downLoadCallback));

    }


    /**
     * 数据写入，开启线程
     *
     * @param body      ResponseBody
     * @param localPath localPath
     * @param callBack  callBack
     */
    private void writeResponseBodyToDisk(ResponseBody body, String localPath,
                                         @NonNull final DownLoadCallback callBack) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        File futureStudioIconFile = new File(localPath);

        try {
            byte[] fileReader = new byte[4096];

            final long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();
            outputStream = new FileOutputStream(futureStudioIconFile);

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

                int currentProcess = (int) (fileSizeDownloaded * 100 * 1.0f / fileSize * 1.0f);
                if (currentProcess != progress) {
                    progress = currentProcess;
                    final long current = fileSizeDownloaded;
                    //在主线程回调下载进度，可直接刷新 UI
                    NetExecutors.getInstance().mainThread().execute(() -> callBack.onDownLoading(fileSize, current, progress));

                    RetrofitLog.i("file download ::" + fileSizeDownloaded + " ; fileSize :" + fileSize + " ; process :" + progress);
                }
            }

            outputStream.flush();

            //写入完成，下载成功
            NetExecutors.getInstance().mainThread().execute(callBack::onDownLoadSuccess);

        } catch (IOException e) {
            RetrofitLog.e("下载异常 IOException=="+e.toString());
            //下载异常
            NetExecutors.getInstance().mainThread().execute(callBack::onDownLoadError);
        } finally {
            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    RetrofitLog.e(" outputStream  IOException=="+e.toString());
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    public String getToken() {
        return null;
    }

    @Override
    public String getTokenKey() { return null; }
}
