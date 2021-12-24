package com.shon.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Date : 2020/7/21 15:11
 * Package name : net.yt.whale.net.request
 * Des : DownLoadApi
 */
interface DownLoadApi {
    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
