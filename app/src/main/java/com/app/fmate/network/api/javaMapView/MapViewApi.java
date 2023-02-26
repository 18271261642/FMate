package com.app.fmate.network.api.javaMapView;

import com.app.fmate.base.AppApi;

import java.util.HashMap;

import retrofit2.Call;

public class MapViewApi extends AppApi<MapViewInterface> {
    public static MapViewApi mapViewApi = new MapViewApi();

    public final Call motionInfoSave(HashMap value) {
        return getApiInterface().motionInfoSave(value);
    }
    public final Call motionInfoGetList(HashMap value) {
        return getApiInterface().motionInfoGetList(value);
    }


}
