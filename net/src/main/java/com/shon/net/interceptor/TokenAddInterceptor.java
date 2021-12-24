package com.shon.net.interceptor;


import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Date : 2020/7/21 12:02
 * Package name : net.yt.whale.net.interceptor
 * Des : 给 请求添加 token 的拦截器
 */
public class TokenAddInterceptor implements Interceptor {
    private String tokenKey = "token";
    private String mToken;

    public TokenAddInterceptor(String token) {
        mToken = token;
    }

    public void updateToken(String newToken) {
        updateToken(tokenKey, newToken);
    }

    /**
     * 2020/8/10  新增
     * 更新 token key
     *
     * @param key    key
     * @param mToken mToken
     */
    public void updateToken(String key, String mToken) {
        if (!TextUtils.isEmpty(key)) {
            tokenKey = key;
        }
        this.mToken = mToken;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        String token = mToken;
        String key = tokenKey;

        builder.addHeader(key, TextUtils.isEmpty(token) ? "" : token); //增加token
        builder.addHeader("osType","1");//Android专用
        Request request = builder.build();

        return chain.proceed(request);
    }
}
