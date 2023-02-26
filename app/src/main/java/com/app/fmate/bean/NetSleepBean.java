package com.app.fmate.bean;

import java.util.List;

public class NetSleepBean {

    /**
     * startTime : 1627488930
     * endTime : 1627515210
     * sleepTime : 07小时18分钟
     * deepSleep : 22
     * lightSleep : 0
     * eye : 0
     * wake : 0
     * apneaTime : 0
     * apneaSecond : 0
     * avgHeartRate : 88
     * minHeartRate : 45
     * maxHeartRate : 99
     * respiratoryQuality : 80
     * sleepList : [{"type":3,"duration":5},{"type":2,"duration":3},{"type":1,"duration":14},{"type":3,"duration":5},{"type":2,"duration":33},{"type":1,"duration":39},{"type":2,"duration":1},{"type":1,"duration":49},{"type":3,"duration":9},{"type":2,"duration":27},{"type":1,"duration":20},{"type":3,"duration":8},{"type":2,"duration":1},{"type":1,"duration":33},{"type":2,"duration":2},{"type":1,"duration":14},{"type":2,"duration":3},{"type":1,"duration":24},{"type":3,"duration":7},{"type":2,"duration":1},{"type":1,"duration":27},{"type":2,"duration":90},{"type":1,"duration":11},{"type":2,"duration":12},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0},{"type":0,"duration":0}]
     */

    private long startTime;
    private long endTime;
    private String sleepTime;
    private int deepSleep;
    private int lightSleep;
    private int eye;
    private int wake;
    private int apneaTime;
    private int apneaSecond;
    private int avgHeartRate;
    private int minHeartRate;
    private int maxHeartRate;
    private int respiratoryQuality;
    private List<SleepListDTO> sleepList;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(int deepSleep) {
        this.deepSleep = deepSleep;
    }

    public int getLightSleep() {
        return lightSleep;
    }

    public void setLightSleep(int lightSleep) {
        this.lightSleep = lightSleep;
    }

    public int getEye() {
        return eye;
    }

    public void setEye(int eye) {
        this.eye = eye;
    }

    public int getWake() {
        return wake;
    }

    public void setWake(int wake) {
        this.wake = wake;
    }

    public int getApneaTime() {
        return apneaTime;
    }

    public void setApneaTime(int apneaTime) {
        this.apneaTime = apneaTime;
    }

    public int getApneaSecond() {
        return apneaSecond;
    }

    public void setApneaSecond(int apneaSecond) {
        this.apneaSecond = apneaSecond;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public int getMinHeartRate() {
        return minHeartRate;
    }

    public void setMinHeartRate(int minHeartRate) {
        this.minHeartRate = minHeartRate;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public int getRespiratoryQuality() {
        return respiratoryQuality;
    }

    public void setRespiratoryQuality(int respiratoryQuality) {
        this.respiratoryQuality = respiratoryQuality;
    }

    public List<SleepListDTO> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<SleepListDTO> sleepList) {
        this.sleepList = sleepList;
    }

    public static class SleepListDTO {
        /**
         * type : 3
         * duration : 5
         */

        private int type;
        private int duration;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
