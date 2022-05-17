package com.example.xingliansdk.utils;

import com.example.xingliansdk.bean.room.BloodPressureHistoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin
 * Date 2022/5/17
 */
public class MapUtils {

    public static String[] halfHourMap = new String[]{"00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30",
    "04:00","04:30","05:00","05:30","06:00","06:30","07:00","07:30","08:00","08:30","09:00",
    "09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00",
    "16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00","22:30","23:00","23:30"};


    public static String[] hourMap = new String[]{"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00",
    "12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};

    public static Map<String, Integer[]> get24HourMap(){
        Map<String,Integer[]> map = new HashMap<>();
        for(int i = 0;i<halfHourMap.length;i++){
            map.put(halfHourMap[i],new Integer[]{0,0});
        }
        return map;
    }

    public static Map<String,Integer[]> getHourMap(){
        Map<String,Integer[]> map = new HashMap<>();
        for(int i = 0;i<24;i++){
            map.put(String.format("%02d",i),null);
        }
        return map;
    }
}
