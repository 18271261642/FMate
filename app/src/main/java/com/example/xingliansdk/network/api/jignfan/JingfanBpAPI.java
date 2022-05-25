package com.example.xingliansdk.network.api.jignfan;

import com.hjq.http.annotation.HttpHeader;
import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/5/25
 */
public class JingfanBpAPI implements IRequestApi {



    @NonNull
    @Override
    public String getApi() {
        return "/jingfan/upload_data";
    }


    private String data;

    private String createTime;

    @HttpHeader
    private String token;
    @HttpHeader
    private String mac;


    public JingfanBpAPI setBp(String data,String time){
        this.data = data;
        this.createTime = time;

        return this;
    }
}
