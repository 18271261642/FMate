package com.app.fmate.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.app.fmate.XingLianApplication;
import com.shon.connector.utils.ShowToast;

import java.util.List;

public class MyLocationUtil {
    private static String provider;

    public static Location getMyLocation() {
//        获取当前位置信息
        //获取定位服务
        LocationManager locationManager = (LocationManager) XingLianApplication.mXingLianApplication.getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(locationManager.GPS_PROVIDER)) {
//            GPS位置控制器
            provider = locationManager.GPS_PROVIDER;//GPS定位
        } else if (list.contains(locationManager.NETWORK_PROVIDER)) {
//            网络位置控制器
            provider = locationManager.NETWORK_PROVIDER;//网络定位
        }

        if (provider != null) {
            if (ActivityCompat.checkSelfPermission( XingLianApplication.mXingLianApplication, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(XingLianApplication.mXingLianApplication, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
            return lastKnownLocation;
        } else {
            ShowToast.INSTANCE.showToastLong("请检查网络或GPS是否打开");
        }


        return null;
    }

}
