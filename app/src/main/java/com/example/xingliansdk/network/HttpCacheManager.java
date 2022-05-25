package com.example.xingliansdk.network;

import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.request.HttpRequest;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2022/03/22
 *    desc   : Http 缓存管理器
 */
public final class HttpCacheManager {



   /**
    * 生成缓存的 key
    */
   public static String generateCacheKey(@NonNull HttpRequest<?> httpRequest) {
      IRequestApi requestApi = httpRequest.getRequestApi();
      return "用户 id" + "\n" + requestApi.getApi() + "\n" + GsonFactory.getSingletonGson().toJson(requestApi);
   }
}