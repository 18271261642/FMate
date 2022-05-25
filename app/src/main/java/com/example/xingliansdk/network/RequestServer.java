package com.example.xingliansdk.network;



import com.example.xingliansdk.BuildConfig;
import com.example.xingliansdk.XingLianApplication;
import com.example.xingliansdk.utils.HelpUtil;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CacheMode;

import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/02
 *    desc   : 服务器配置
 */
public class RequestServer implements IRequestServer {


    public RequestServer(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    private BodyType bodyType = BodyType.FORM;

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    @Override
    public String getHost() {
       if(BuildConfig.BUILD_TYPE.equals("debug")){
            return BuildConfig.baseUrlDev;
        }else{
            return BuildConfig.baseUrl;
        }

    }


    @NonNull
    @Override
    public CacheMode getCacheMode() {
        return CacheMode.NO_CACHE;
    }
}