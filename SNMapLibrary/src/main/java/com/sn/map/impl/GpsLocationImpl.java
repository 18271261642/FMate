package com.sn.map.impl;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import com.sn.map.bean.SNLocation;
import com.sn.map.interfaces.ILocation;
import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * 功能:GPS定位
 */
public class GpsLocationImpl implements ILocation {

    private final WeakReference<Context> contextWeakReference;
    private LocationManager locationManager;
    private int minTime = 3000;
    private int minDistance = 10;
    private LocationListener listener;


    public GpsLocationImpl(Context context, int minTime, int minDistance) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
        this.minTime = minTime;
        this.minDistance = minDistance;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void start() {

        try {
            //绑定监听状态
            locationManager.addGpsStatusListener(mGpsListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        if (locationManager != null) {
            if (mLocationListener != null) {
                locationManager.removeUpdates(mLocationListener);
            }
        }
    }

    @Override
    public SNLocation getLastLocation() {

        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation == null) return null;
            return new SNLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setLocationListener(LocationListener listener) {
        this.listener = listener;
    }

    private GpsStatus.Listener mGpsListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    int count = 0;
                    int can_use = 0;
                    try {
                        //获取当前状态
                        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                        //获取卫星颗数的默认最大值
                        int maxSatellites = gpsStatus.getMaxSatellites();
                        //创建一个迭代器保存所有卫星
                        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                        while (iters.hasNext() && count <= maxSatellites) {
                            GpsSatellite s = iters.next();
                            float snr = s.getSnr();
                            if (snr > 30) {
                                can_use++;
                                if (can_use >= 4) {
                                    //表示有信号
                                } else {
                                    //信号弱或无信号
                                }
                            }
                            count++;
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if (listener != null) {
                        listener.onSignalChanged(can_use);
                    }
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
            }
        }

    };
    private android.location.LocationListener mLocationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (listener != null && location != null) {
                if (location.getLongitude() == 0 || location.getLatitude() == 0) {
                    return;
                }
                //封装成通用数据
                SNLocation snLocation = new SNLocation();
                snLocation.setLatitude(location.getLatitude());
                snLocation.setLongitude(location.getLongitude());
                snLocation.setAccuracy(location.getAccuracy());
                snLocation.setSpeed(location.getSpeed());
                snLocation.setTime(location.getTime());

                listener.onLocationChanged(snLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


}
