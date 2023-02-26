package com.app.fmate.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class GPSUtil {
    private static LocationManager locationManager;

    private static LocationManager getLocationManager(Context context) {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }


    public static void registerGpsStatus(Context context,GpsStatus.Listener listener) {
        locationManager = getLocationManager(context);
        if (locationManager == null)
            return;
        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.addGpsStatusListener(listener);
        }
    }

    public static void unregisterGpsListener(GpsStatus.Listener listener) {
        if (locationManager != null) {
            locationManager.removeGpsStatusListener(listener);
        }

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
