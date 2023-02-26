package com.app.fmate.base.activity;

import com.orhanobut.hawk.Hawk;
import com.shon.net.ITokenHandler;
import com.shon.net.util.RetrofitLog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Date : 2020/8/1 14:16
 * Package name : net.yt.whale.net.interceptor
 */
public class GetHeadInterceptor implements Interceptor {
    private ITokenHandler iTokenHandler;

    public GetHeadInterceptor(ITokenHandler tokenHandler) {
        this.iTokenHandler = tokenHandler;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
      String token=  response.header("vtoken");
//        String vtknExpiryTime=  response.header("vtknExpiryTime");
        RetrofitLog.i("token+="+response.headers("vtoken"));
        RetrofitLog.i("vtknExpiryTime+="+response.header("vtknExpiryTime"));
        Hawk.put("vtoken",token);
        //int code = response.code();
//        if (isTokenExpired(code)) {//根据和服务端的约定判断token过期
//            RetrofitLog.e("拦截到 token  已过期");
//            if (iTokenHandler != null) {
//                iTokenHandler.onTokenError();
//            }
//        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response response
     * @return 是否失效
     */
    private boolean isTokenExpired(int response) {
        return response == 201;
    }

}