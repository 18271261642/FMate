package com.shon.connector.bean;

import java.util.Arrays;

/**
 * Created by Admin
 * Date 2022/3/17
 */
public class SpecifySleepSourceBean {

    private String remark;

    private long startTime;

    private long endTime;

    private int[] avgActive;

    private int[] avgHeartRate;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public int[] getAvgActive() {
        return avgActive;
    }

    public void setAvgActive(int[] avgActive) {
        this.avgActive = avgActive;
    }

    public int[] getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int[] avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    @Override
    public String toString() {
        return "SpecifySleepSourceBean{" +
                "remark='" + remark + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", avgActive=" + Arrays.toString(avgActive) +
                ", avgHeartRate=" + Arrays.toString(avgHeartRate) +
                '}';
    }
}
