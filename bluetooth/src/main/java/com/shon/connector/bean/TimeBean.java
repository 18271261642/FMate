package com.shon.connector.bean;

public class TimeBean {
    /**
     * 闹钟特征值
     */
    public static byte ALARM_FEATURES = 0x01;
    /**
     * 闹钟特征值unicode编码
     */
    public static byte ALARM_FEATURES_UNICODE = 0x31;
    /**
     * 日程特征值
     */
    public static int SCHEDULE_FEATURES = 0x02;

    /**
     * 日程特征值unicode编码
     */
    public static byte SCHEDULE_FEATURES_UNICODE = 0x32;

    /**
     * 开关
     */
    public int mSwitch; //0默认 1关 2开
    /**
     * 周一至周日是否开启并且是否闹钟只执行一次
     */
    public int specifiedTime;
    public String specifiedTimeDescription;
    public String getSpecifiedTimeDescription() {
        return specifiedTimeDescription;
    }

    public void setSpecifiedTimeDescription(String specifiedTimeDescription) {
        this.specifiedTimeDescription = specifiedTimeDescription;
    }
    /**
     * 开始时针
     */
    public int openHour;
    /**
     * 开始分针
     */
    public int openMin;
    /**
     * 关闭时针
     */
    public int closeHour;
    /**
     * 关闭分针
     */
    public int closeMin;
    /**
     * 提醒间隔
     */
    public int reminderInterval;
    /**
     * 闹钟编号
     */
    public int number;

    /**
     * 特征
     */
    public int characteristic;

    /**
     * unicode 编码
     */
    public String unicode;
    /**
     * unicode 编码类型
     */
    public byte unicodeType;
    public int year;
    public int month;
    public int day;
    public int hours;
    public int min;
    public int ss;
    /**
     * 勿扰模式
     */
    public int doNotDisturb;
    /**
     * 夜间模式
     */
    public int nightMode;
    /**
     * 开始时间戳
     */
    public long startTime;
    /**
     * 结束时间戳
     */
    public long endTime;
    /**
     * 时间间隔
     */
    public int timeInterval;
    /**
     * 数据单元的实际长度
     */
    public int dataUnitType;
    /**
     * 提醒周期
     */
    public  int ReminderPeriod;
    public int getReminderPeriod() {
        return ReminderPeriod;
    }

    public void setReminderPeriod(int reminderPeriod) {
        ReminderPeriod = reminderPeriod;
    }

    public int getDataUnitType() {
        return dataUnitType;
    }

    public void setDataUnitType(int dataUnitType) {
        this.dataUnitType = dataUnitType;
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

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getDoNotDisturb() {
        return doNotDisturb;
    }

    public void setDoNotDisturb(int doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }

    public int getNightMode() {
        return nightMode;
    }

    public void setNightMode(int nightMode) {
        this.nightMode = nightMode;
    }


    public int getSs() {
        return ss;
    }

    public void setSs(int ss) {
        this.ss = ss;
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

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public byte getUnicodeType() {
        return unicodeType;
    }

    public void setUnicodeType(byte unicodeType) {
        this.unicodeType = unicodeType;
    }


    public String getUnicode() {

        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public int getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(int characteristic) {
        this.characteristic = characteristic;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }



    public int getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(int reminderInterval) {
        this.reminderInterval = reminderInterval;
    }

    public int getSwitch() {
        return mSwitch;
    }

    public void setSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
    }

    public int getSpecifiedTime() {
        return specifiedTime;
    }

    public void setSpecifiedTime(int specifiedTime) {
        this.specifiedTime = specifiedTime;
    }

    public int getOpenHour() {
        return openHour;
    }

    public void setOpenHour(int openHour) {
        this.openHour = openHour;
    }

    public int getOpenMin() {
        return openMin;
    }

    public void setOpenMin(int openMin) {
        this.openMin = openMin;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    public int getCloseMin() {
        return closeMin;
    }

    public void setCloseMin(int closeMin) {
        this.closeMin = closeMin;
    }


}
