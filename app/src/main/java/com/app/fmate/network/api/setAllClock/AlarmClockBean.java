package com.app.fmate.network.api.setAllClock;

public class AlarmClockBean {
    int characteristic;
    int hours;
    int mSwitch;
    int min;
    int number;
    int specifiedTime;
    String unicode;
    int unicodeType;
    String specifiedTimeDescription;
    long endTime;

    ///以下是日程的
    int year;
    int month;
    int day;

    public AlarmClockBean() {
    }

    //用于日程
    public AlarmClockBean(int characteristic, int mSwitch, int number, String unicode, int unicodeType, long endTime, int year, int month, int day, int hours, int min) {
        this.characteristic = characteristic;
        this.hours = hours;
        this.mSwitch = mSwitch;
        this.min = min;
        this.number = number;
        this.unicode = unicode;
        this.unicodeType = unicodeType;
        this.endTime = endTime;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    //用于闹钟
    public AlarmClockBean(int characteristic, int hours, int mSwitch, int min, int number, int specifiedTime, String unicode, int unicodeType, String specifiedTimeDescription, long endTime/*,long creatureTime*/) {
        this.characteristic = characteristic;
        this.hours = hours;
        this.mSwitch = mSwitch;
        this.min = min;
        this.number = number;
        this.specifiedTime = specifiedTime;
        this.unicode = unicode;
        this.unicodeType = unicodeType;
        this.specifiedTimeDescription = specifiedTimeDescription;
        this.endTime = endTime;
//        this.creatureTime=creatureTime;
    }

    @Override
    public String toString() {
        return "AlarmClockBean{" +
                "characteristic=" + characteristic +
                ", hours=" + hours +
                ", mSwitch=" + mSwitch +
                ", min=" + min +
                ", number=" + number +
                ", specifiedTime=" + specifiedTime +
                ", unicode='" + unicode + '\'' +
                ", unicodeType=" + unicodeType +
                ", specifiedTimeDescription='" + specifiedTimeDescription + '\'' +
                ", endTime=" + endTime +
                '}';
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(int characteristic) {
        this.characteristic = characteristic;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getmSwitch() {
        return mSwitch;
    }

    public void setmSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSpecifiedTime() {
        return specifiedTime;
    }

    public void setSpecifiedTime(int specifiedTime) {
        this.specifiedTime = specifiedTime;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public int getUnicodeType() {
        return unicodeType;
    }

    public void setUnicodeType(int unicodeType) {
        this.unicodeType = unicodeType;
    }

    public String getSpecifiedTimeDescription() {
        return specifiedTimeDescription;
    }

    public void setSpecifiedTimeDescription(String specifiedTimeDescription) {
        this.specifiedTimeDescription = specifiedTimeDescription;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
