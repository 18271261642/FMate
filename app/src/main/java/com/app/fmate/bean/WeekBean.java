package com.app.fmate.bean;

public class WeekBean {
    private  long time;
    private  long totalStep;
    private int year;
    private int month;
    private String date;
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTotalStep() {
        return totalStep;
    }

    public void setTotalStep(long totalStep) {
        this.totalStep = totalStep;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public WeekBean(long time, long totalStep, int year, int month) {
        this.time = time;
        this.totalStep = totalStep;
        this.year = year;
        this.month = month;
    }

    public WeekBean(long time, long totalStep) {
        this.time = time;
        this.totalStep = totalStep;
    }

    public WeekBean(long time, long totalStep, String date) {
        this.time = time;
        this.totalStep = totalStep;
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeekBean{" +
                "time=" + time +
                ", totalStep=" + totalStep +
                '}';
    }
}
