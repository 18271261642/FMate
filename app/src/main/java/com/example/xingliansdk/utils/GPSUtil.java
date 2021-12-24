package com.example.xingliansdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

public class GPSUtil {
    private static LocationManager locationManager;

    private static LocationManager getLocationManager(Context context) {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    /**
     * 是否开启GPS了
     * @param context
     * @return
     */
    public static boolean isGpsEnable(Context context){
        if (getLocationManager(context)==null) {
            return false;
        }
        // 判断GPS是否正常启动
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开GPS设置
     * @param activity
     */
    public static void openGpsSetting(Activity activity){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, 0);
    }

}
