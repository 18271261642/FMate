package com.app.fmate.ui.setting.heartRateAlarm;

public class HeartRateAlarmBean {
    int mSwitch;
    int heartNum;

    public HeartRateAlarmBean() {
    }

    public HeartRateAlarmBean(int mSwitch, int heartNum) {
        this.mSwitch = mSwitch;
        this.heartNum = heartNum;
    }

    public int getmSwitch() {
        return mSwitch;
    }

    public void setmSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
    }

    public int getHeartNum() {
        return heartNum;
    }

    public void setHeartNum(int heartNum) {
        this.heartNum = heartNum;
    }
}
