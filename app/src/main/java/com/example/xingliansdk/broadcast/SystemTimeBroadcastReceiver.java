package com.example.xingliansdk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.xingliansdk.blesend.BleSend;
import com.example.xingliansdk.view.DateUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.bean.TimeBean;

import java.util.Calendar;
import java.util.Date;


/**
 * 监听系统时间变化的广播
 */
public class SystemTimeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)
            return;
        Log.e("TIME","-------时间变化="+action);
        if(action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)){

            Log.e("TIME","----时间="+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

            TimeBean time = new TimeBean();
            long currentTime = System.currentTimeMillis();
            time.setYear(DateUtil.getYear(currentTime));
            time.setMonth(DateUtil.getMonth(currentTime));
            time.setDay(DateUtil.getDay(currentTime));
            time.setMin(DateUtil.getMinute(currentTime));
            time.setHours(DateUtil.getHour(currentTime));
            time.setSs(DateUtil.getSecond(currentTime));
            BleWrite.writeTimeCall(time);
        }
    }
}
