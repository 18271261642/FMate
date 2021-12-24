package com.example.xingliansdk.bean;

import java.io.Serializable;

public class SleepTypeBean implements Serializable {
    String name;
    String timeDate;
    String status;
    String referValue;
    int type;

    public SleepTypeBean() {
    }

    public SleepTypeBean(String name, String timeDate, String status, String referValue, int type) {
        this.name = name;
        this.timeDate = timeDate;
        this.status = status;
        this.referValue = referValue;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferValue() {
        return referValue;
    }

    public void setReferValue(String referValue) {
        this.referValue = referValue;
    }
}
