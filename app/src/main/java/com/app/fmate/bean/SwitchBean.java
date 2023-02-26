package com.app.fmate.bean;

public class SwitchBean {
    public SwitchBean() {
    }

    byte key = 0x00;
    int mSwitch = 0;

    public SwitchBean(byte key, int mSwitch) {
        this.key = key;
        this.mSwitch = mSwitch;
    }

    public byte getKey() {
        return key;
    }

    public void setKey(byte key) {
        this.key = key;
    }

    public int getSwitch() {
        return mSwitch;
    }

    public void setSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
    }
}
