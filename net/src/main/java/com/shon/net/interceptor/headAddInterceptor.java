package com.shon.net.interceptor;

import android.text.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class headAddInterceptor implements Interceptor {
    private String tokenKey = "token";
    private String mToken;
    private String mac;
    public headAddInterceptor(String token, String mac) {
        mToken = token;
        this.mac=mac;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String token = mToken;
        String key = tokenKey;

        builder.addHeader("authorization", TextUtils.isEmpty(token) ? "" : token); //增加token
        builder.addHeader("MAC", TextUtils.isEmpty(mac) ? "" : mac); //增加mac
        builder.addHeader("osType","1");//Android
        Request request = builder.build();
//        TLog.Companion.error("re++"+request.headers());
        return chain.proceed(request);

    }
}
