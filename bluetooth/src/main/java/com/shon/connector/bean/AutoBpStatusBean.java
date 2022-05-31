package com.shon.connector.bean;

/**
 * 血压自动测量的状态bean
 * Created by Admin
 * Date 2022/5/8
 */
public class AutoBpStatusBean {

    //夜间自动测量开关  0x00-默认；0x01-关闭；0x02-打开
    private byte nightBpStatus;

    //非睡眠自动时间测量开关  0x00-默认；0x01-关闭；0x02-打开
    private byte normalBpStatus;

    //开启时
    private int startHour;
    //开启分
    private int startMinute;
    //关闭时
    private int endHour;
    //关闭分
    private int endMinute;
    //间隔
    private int bpInterval;

    public AutoBpStatusBean() {
    }


    public AutoBpStatusBean(byte nightBpStatus, byte normalBpStatus, int startHour, int startMinute, int endHour, int endMinute, int bpInterval) {
        this.nightBpStatus = nightBpStatus;
        this.normalBpStatus = normalBpStatus;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.bpInterval = bpInterval;
    }

    public byte getNightBpStatus() {
        return nightBpStatus;
    }

    public void setNightBpStatus(byte nightBpStatus) {
        this.nightBpStatus = nightBpStatus;
    }

    public byte getNormalBpStatus() {
        return normalBpStatus;
    }

    public void setNormalBpStatus(byte normalBpStatus) {
        this.normalBpStatus = normalBpStatus;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getBpInterval() {
        return bpInterval;
    }

    public void setBpInterval(int bpInterval) {
        this.bpInterval = bpInterval;
    }


    @Override
    public String toString() {
        return "AutoBpStatusBean{" +
                "nightBpStatus=" + nightBpStatus +
                ", normalBpStatus=" + normalBpStatus +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", bpInterval=" + bpInterval +
                '}';
    }
}
