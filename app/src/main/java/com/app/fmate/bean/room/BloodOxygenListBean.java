package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BloodOxygenListBean")
public class BloodOxygenListBean {
    public static long day = 86400;
    @PrimaryKey
//    @ColumnInfo(name = "startTime")
    long startTime;
    long endTime;
    String array;
    Boolean isAllDay = false;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean AllDay) {
        isAllDay = AllDay;
    }

    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
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


    public BloodOxygenListBean() {
    }

    public BloodOxygenListBean(long startTime, long endTime, String array) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.array = array;
        if (endTime - startTime >= day) {
            setAllDay(true);

        }
    }

    public BloodOxygenListBean(long startTime, long endTime, String array, Boolean isAllDay, String dateTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.array = array;
        this.isAllDay = isAllDay;
        this.dateTime = dateTime;
    }
}
