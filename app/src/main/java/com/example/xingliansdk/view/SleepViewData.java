package com.example.xingliansdk.view;

import android.content.Context;

import com.example.xingliansdk.R;

import java.io.Serializable;

/**
 * 睡眠数据，SleepView使用
 */
public class SleepViewData
        implements Serializable
{
    // 开始时间
    String beginTime;
    //结束时间
    String endTime;
    //持续分钟
    int duration;
    //睡眠质量,直接把计算好的结果存放在这里
    //每个数除以5，结果取整，0-2 是深睡，3-8是浅睡，9-20是快速眼动，>20是清醒。
    //1:清醒 2:快速眼动 3:浅睡 4:深睡
    int status;
    float startX;
    float startY;
    float stopX;
    float stopY;

    public SleepViewData() {
    }

    public SleepViewData(String beginTime, String endTime, int duration, int status) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.duration = duration;
        this.status = status;
    }
    public SleepViewData(  int duration, int status) {
        this.duration = duration;
        this.status = status;
    }
    public String getBeginTime() {
        return this.beginTime == null?"":this.beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return this.endTime == null?"":this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStopX() {
        return stopX;
    }

    public void setStopX(float stopX) {
        this.stopX = stopX;
    }

    public float getStopY() {
        return stopY;
    }

    public void setStopY(float stopY) {
        this.stopY = stopY;
    }

    @Override
    public String toString() {
        return "SleepData{" +
                "beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + duration +
                ", status=" + status +
                ", startX=" + startX +
                ", startY=" + startY +
                ", stopX=" + stopX +
                ", stopY=" + stopY +
                '}';
    }


    //1:清醒 2:快速眼动 3:浅睡 4:深睡
    public  String getSleepStatus(Context context){
        if(status == 1)
            return context.getResources().getString(R.string.string_sleep_deep);
        if(status == 2)
            return context.getResources().getString(R.string.string_sleep_light);
        if(status == 3)
            return context.getResources().getString(R.string.string_sleep_eye);
        if(status == 4)
            return context.getResources().getString(R.string.string_sleep_awake);
        if(status == 5)
            return context.getResources().getString(R.string.string_sleep_into_time);
        return "";
    }
}