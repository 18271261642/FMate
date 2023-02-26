package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PressureListBean")
public class
PressureListBean {
    public static long day = 86400;
    @PrimaryKey
    long startTime;
    long endTime;
    String pressure;
    Boolean isAllDay = false;
    String dateTime;
    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean AllDay) {
        isAllDay = AllDay;
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


    public PressureListBean() {
    }

//    public PressureListBean(long startTime, long endTime, String pressure, Boolean isAllDay ) {
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.pressure = pressure;
//        this.isAllDay=isAllDay;
////        if (endTime - startTime >= day) {
////            this.isAllDay = isAllDay;
////        }
//    }

    public PressureListBean(long startTime, long endTime, String pressure, Boolean isAllDay, String dateTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pressure = pressure;
        this.isAllDay = isAllDay;
        this.dateTime = dateTime;
    }
}
