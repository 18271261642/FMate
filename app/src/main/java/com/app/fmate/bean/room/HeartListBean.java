package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "heartListBean")
public class HeartListBean {
    public static long day = 86400;
    @PrimaryKey
    long startTime;
    long endTime;
    String heart;
    Boolean isAllDay = false;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * 类型 比如心率为type=1 ,type=2为血氧
     */
    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean AllDay) {
        isAllDay = AllDay;
    }

    public String getHeart() {
        return heart;
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

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public HeartListBean() {
    }

//
//    public HeartListBean(long startTime, long endTime, String heart, boolean isAllDay) {
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.heart = heart;
//        if (endTime - startTime >= day) {
//            this.isAllDay = isAllDay;
//        }
//    }

    public HeartListBean(long startTime, long endTime, String heart, Boolean isAllDay, String dateTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.heart = heart;
        this.isAllDay = isAllDay;
        this.dateTime = dateTime;
    }
}
