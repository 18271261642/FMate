package com.example.xingliansdk.utils;

import android.util.Log;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2021/9/11
 */
public class Utils {

    private static final String TAG = "Utils";

    private static double miV = 0.6213;
    private static double KmV = 1.609344;

    private static final DecimalFormat deci = new DecimalFormat("##");

    //公制转英制 1km = 0.6213英里
    public static double kmToMile(double kmValue){
        return mul(kmValue,miV,2);
    }


    //英制转公制
    public static double miToKm(double miValue){
        return mul(miValue,KmV,2);
    }


    /**
     * 两个double相乘
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }
    public static Double mul(Double v1, Double v2,int point) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).setScale(point, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static String matchPace(double speed){
        try {
            if(speed == 0.0){
                return "--";
            }
            double pace = divi(1,speed,2);
            pace = mul(pace,Double.valueOf(60));
            pace = divi(pace,1,2); //保留两位小数
            if(String.valueOf(pace).contains(".")){
                String secondStr = StringUtils.substringAfter(String.valueOf(pace),".");
                int secondInt = Integer.parseInt(secondStr.trim());
                int secondS = secondInt * 60/10;

                String minuteStr = StringUtils.substringBefore(String.valueOf(pace),".");
                return minuteStr+"'"+secondS+"''";
            }
//        int minute = (int) (pace / 60);
//        int second = (int) (pace % 60);
//        return minute+"'"+second+"''";

            return "--";
        }catch (Exception e){
            e.printStackTrace();
            return "--";
        }

    }

    /**
     * 小数相加
     * @param v1
     * @param v2
     * @return
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.add(b2).doubleValue();

    }


    /**
     * 小数相乘
     * @param v1
     * @param v2
     * @return
     */
    public static double muiltip(double v1, double v2) {

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.multiply(b2).doubleValue();

    }



    /**
     * 两个double相除，保留小数
     * @param d1 被除数
     * @param d2 除数
     * @param point 保留几位小数
     * @return
     */
    public static double divi(double d1,double d2,int point){
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2,point, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     */
    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(now);
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式时间
     *
     * @return
     */
    public static String getCurrentDate1() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取H:mm:ss格式时间
     *
     * @return
     */
    public static String getCurrentDateByFormat(String format) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 根据当前日期获取左右一天的日期
     *
     * @param date 条件日期
     * @param left true_前一天 false_后一天
     * @return 计算后的日期
     */
    public static String obtainAroundDate(String date, boolean left) {
        if (date.equals(obtainFormatDate(0)) && !left) {
            return date;//如果传入的日期==今天,而且要求后一天数据,直接返回今天
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date dateTemp = null;
        try {
            dateTemp = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateTemp == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTemp);// 初始化日历
        calendar.add(Calendar.DATE, left ? -1 : 1);
        dateTemp = calendar.getTime();
        return sdf.format(dateTemp);
    }

    /**
     * 根据类型获取指定日期
     *
     * @param type 0_今天 1_昨天 2_前天
     * @return "yyyy-MM-dd"
     */
    public static String obtainFormatDate(int type) {
        Date date = new Date();// 当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);// 初始化日历
        calendar.add(Calendar.DATE, 0 - type);// 0,-1,-2
        date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }


    /**
     * 格式化为HH:mm格式
     * @param sourceTime 时间
     * @return
     */
    public static String formatCusTime(String sourceTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm",Locale.CHINA);
        Date dateTemp = null;
        try {
            dateTemp = sdf.parse(sourceTime);
            assert dateTemp != null;
            return sdf2.format(dateTemp);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 格式化为MM/dd格式
     * @param sourceTime 时间，
     * @return
     */
    public static String formatCusTimeForDay(String sourceTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd",Locale.CHINA);
        Date dateTemp = null;
        try {
            dateTemp = sdf.parse(sourceTime);
            assert dateTemp != null;
            return sdf2.format(dateTemp);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 转换样式
     * @param sourceTime
     * @return
     */
    public static String formatCusTimeForDay(String sourceTime,String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat sdf2 = new SimpleDateFormat(endTime,Locale.CHINA);
        Date dateTemp = null;
        try {
            dateTemp = sdf.parse(sourceTime);
            assert dateTemp != null;
            return sdf2.format(dateTemp);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
