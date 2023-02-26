package com.app.fmate.ui.bp;


import java.io.Serializable;


public class MeasureBpBean implements Serializable {


    private String date;

    private String time;

    private float sbp;

    private float dbp;

    private float heartRate;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getSbp() {
        return sbp;
    }

    public void setSbp(float sbp) {
        this.sbp = sbp;
    }

    public float getDbp() {
        return dbp;
    }

    public void setDbp(float dbp) {
        this.dbp = dbp;
    }

    public float getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(float heartRate) {
        this.heartRate = heartRate;
    }
}



