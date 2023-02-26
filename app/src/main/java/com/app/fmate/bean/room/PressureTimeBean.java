package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.shon.connector.bean.TimeBean;

@Entity(tableName = "PressureTimeBean")
public class PressureTimeBean extends TimeBean {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    String mList;
    //心率
    Boolean isStatus = false;

    public Boolean getStatus() {
        return isStatus;
    }

    public void setStatus(Boolean status) {
        isStatus = status;
    }

    public String getmList() {
        return mList;
    }

    public void setmList(String mList) {
        this.mList = mList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PressureTimeBean() {
    }

    public PressureTimeBean(int dataUnitLength, int timeInterval, long startTime, long endTime) {
        this.dataUnitType = dataUnitLength;
        this.timeInterval = timeInterval;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PressureTimeBean(Integer id, int dataUnitLength, int timeInterval, long startTime, long endTime) {
        this.id = id;
        this.dataUnitType = dataUnitLength;
        this.timeInterval = timeInterval;
        this.startTime = startTime;
        this.endTime = endTime;
    }


}
