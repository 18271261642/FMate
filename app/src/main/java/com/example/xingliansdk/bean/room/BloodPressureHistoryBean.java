package com.example.xingliansdk.bean.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "BloodPressureHistoryBean")
public class
BloodPressureHistoryBean {
    @Ignore
    public static long day = 86400;
    @PrimaryKey
    @NotNull
    long startTime;
    @NotNull
    long endTime;
    /**
     * 用于处于正常偏高放松等状态的图标展示 现在版本阉割
     */
    int type;
    /**
     * 收缩压
     */
    int systolicBloodPressure;
    /**
     * 舒张压
     */
    int diastolicBloodPressure;
    String dateTime;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public void setSystolicBloodPressure(int systolicBloodPressure) {
        this.systolicBloodPressure = systolicBloodPressure;
    }

    public int getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public void setDiastolicBloodPressure(int diastolicBloodPressure) {
        this.diastolicBloodPressure = diastolicBloodPressure;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }


    public BloodPressureHistoryBean() {
    }

    public BloodPressureHistoryBean(long startTime, long endTime, int type, int systolicBloodPressure, int diastolicBloodPressure, String dateTime) {
        this.startTime = startTime;
        this.endTime = endTime;
//        this.isAllDay = isAllDay;
        this.type = type;
        this.systolicBloodPressure = systolicBloodPressure;
        this.diastolicBloodPressure = diastolicBloodPressure;
        this.dateTime = dateTime;
//        if (endTime - startTime >= day) {
//            this.isAllDay = isAllDay;
//        }
    }

}
