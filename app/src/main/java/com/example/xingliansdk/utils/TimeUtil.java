package com.example.xingliansdk.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.String.valueOf;

public class TimeUtil {


    public static long formatTimeToLong(String time){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm",Locale.CHINA);
            return simpleDateFormat.parse(time).getTime()/1000;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }


    /*
     * 将时间戳转换为时间
     *
     * s就是时间戳
     */

    public static String stampToDate(long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        //如果它本来就是long类型的,则不用写这一步
        long lt = new Long(valueOf(s));
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间
     *
     * s就是时间戳
     */

    public static String stampToDateS(String s, String fprmat) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fprmat);
        //如果它本来就是long类型的,则不用写这一步
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    @SuppressLint("SimpleDateFormat")
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = valueOf(ts);
        return res;
    }

    /**
     * 获取当天0时0分的时间戳
     * @return
     */
    public static long getTodayZero(int day) {
        Date date = new Date();
        long l = 24*60*60*1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000-day*l);
    }
}
