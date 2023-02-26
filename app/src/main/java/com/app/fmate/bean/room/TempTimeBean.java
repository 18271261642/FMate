package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TempTimeBean")
public class TempTimeBean  {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    int dataUnitType;
    int timeInterval;
    long startTime;
    long endTime;

    public int getDataUnitType() {
        return dataUnitType;
    }

    public void setDataUnitType(int dataUnitType) {
        this.dataUnitType = dataUnitType;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TempTimeBean() {
    }

    public TempTimeBean(int dataUnitType, int timeInterval, long startTime, long endTime) {
        this.dataUnitType = dataUnitType;
        this.timeInterval = timeInterval;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TempTimeBean(Integer id, int dataUnitType, int timeInterval, long startTime, long endTime) {
        this.id = id;
        this.dataUnitType = dataUnitType;
        this.timeInterval = timeInterval;
        this.startTime = startTime;
        this.endTime = endTime;
    }


}
