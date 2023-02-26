package com.app.fmate.bean;

public class YearBean {
    private int year;
    private int month;
    private long totalStep;
    private Double weight;
    YearBean() {
    }

    public YearBean(int year, int month, long totalStep) {
        this.year = year;
        this.month = month;
        this.totalStep = totalStep;
    }
    public YearBean(int year, int month, Double weight) {
        this.year = year;
        this.month = month;
        this.weight = weight;
    }
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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

    @Override
    public String toString() {
        return "YearBean{" +
                "year=" + year +
                ", month=" + month +
                ", totalStep=" + totalStep +
                '}';
    }
}
