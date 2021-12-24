package com.sn.map.utils;

import android.content.Context;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;

/**
 * 功能:位置转换器
 */

public class LocationConverter {
    /**
     * gps转谷歌
     * @param latitude
     * @param longitude
     * @return
     */
    public static double[] Gps2Google(double latitude,double longitude) {
        return new double[]{latitude,longitude};
    }
    /**
     * gps转高德
     * @param latitude
     * @param longitude
     * @return
     */
    public static double[] Gps2Gaode(Context context,double latitude, double longitude) {
        //TODO GPS转高德地图
        CoordinateConverter converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(new LatLng(latitude, longitude));
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return new double[]{desLatLng.latitude,desLatLng.longitude};
    }
}
