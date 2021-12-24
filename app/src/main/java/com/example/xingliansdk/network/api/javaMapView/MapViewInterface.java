package com.example.xingliansdk.network.api.javaMapView;

import com.example.xingliansdk.BaseData;
import com.example.xingliansdk.network.BaseResult;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface MapViewInterface {
    @GET("/motion_info/get_list")
    Call<BaseResult> motionInfoGetList(
            @QueryMap HashMap<String, Object> value
    );


    @FormUrlEncoded
    @POST("/motion_info/save")
    Call<BaseData> motionInfoSave(
            @FieldMap HashMap<String, Object> value
    );
}
