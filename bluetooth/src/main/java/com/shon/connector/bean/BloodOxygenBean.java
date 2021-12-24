package com.shon.connector.bean;

public class BloodOxygenBean {
    int type;
    int compressionLong;
    int notCompressionLong;
    int interval;
    long startTime;
    long endTime;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCompressionLong() {
        return compressionLong;
    }

    public void setCompressionLong(int compressionLong) {
        this.compressionLong = compressionLong;
    }

    public int getNotCompressionLong() {
        return notCompressionLong;
    }

    public void setNotCompressionLong(int notCompressionLong) {
        this.notCompressionLong = notCompressionLong;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
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


}
