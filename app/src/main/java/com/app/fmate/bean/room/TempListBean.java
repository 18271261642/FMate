package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TempListBean")
public class TempListBean {
    @PrimaryKey
    long startTime;
    long endTime;
    String array;
    Integer id;
    String  dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public TempListBean() {
    }


    public TempListBean(long startTime,
                        long endTime,
                        String array,
                        String  dateTime) {
        this.startTime=startTime;
        this.endTime=endTime;
        this.array=array;
        this.dateTime=dateTime;
    }
}
