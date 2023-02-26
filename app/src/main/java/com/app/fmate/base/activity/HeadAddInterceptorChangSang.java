package com.app.fmate.base.activity;

import android.text.TextUtils;

import com.shon.connector.utils.TLog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeadAddInterceptorChangSang implements Interceptor {
    private String mToken;
    public HeadAddInterceptorChangSang(String token ) {
        mToken = token;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String token = mToken;
        builder.addHeader("vtoken", TextUtils.isEmpty(token) ? "" : token); //增加token
        Request request = builder.build();
        TLog.Companion.error("changsan++"+request.headers());
        return chain.proceed(request);
    }
}
