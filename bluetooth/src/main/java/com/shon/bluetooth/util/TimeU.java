package com.shon.bluetooth.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2022/5/12
 */
public class TimeU {


    public static String getCurrTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return simpleDateFormat.format(new Date(time));
    }

}
