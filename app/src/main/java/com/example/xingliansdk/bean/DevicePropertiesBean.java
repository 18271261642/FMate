package com.example.xingliansdk.bean;

public class DevicePropertiesBean {
    int electricity;
    int mCurrentBattery;
    int mDisplayBattery;
    int type;
    public int getElectricity() {
        return electricity;
    }

    public void setElectricity(int electricity) {
        this.electricity = electricity;
    }

    public int getmCurrentBattery() {
        return mCurrentBattery;
    }

    public void setmCurrentBattery(int mCurrentBattery) {
        this.mCurrentBattery = mCurrentBattery;
    }

    public int getmDisplayBattery() {
        return mDisplayBattery;
    }

    public void setmDisplayBattery(int mDisplayBattery) {
        this.mDisplayBattery = mDisplayBattery;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public DevicePropertiesBean(){}
    public DevicePropertiesBean(  int electricity,
            int mCurrentBattery,
            int mDisplayBattery,
            int type ){
        this.electricity=electricity;
        this.mCurrentBattery=mCurrentBattery;
        this.mDisplayBattery=mDisplayBattery;
        this.type=type;

    }

    @Override
    public String toString() {
        return "DevicePropertiesBean{" +
                "electricity=" + electricity +
                ", mCurrentBattery=" + mCurrentBattery +
                ", mDisplayBattery=" + mDisplayBattery +
                ", type=" + type +
                '}';
    }
}
