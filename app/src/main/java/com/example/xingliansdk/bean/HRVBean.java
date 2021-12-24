package com.example.xingliansdk.bean;

public class HRVBean {
    int heartRate;
    int testHeartRate;
    long rrOne;
    long rrTwo;

    public int getTestHeartRate() {
        return testHeartRate;
    }

    public void setTestHeartRate(int testHeartRate) {
        this.testHeartRate = testHeartRate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public long getRrOne() {
        return rrOne;
    }

    public void setRrOne(long rrOne) {
        this.rrOne = rrOne;
    }

    public long getRrTwo() {
        return rrTwo;
    }

    public void setRrTwo(long rrTwo) {
        this.rrTwo = rrTwo;
    }

}
