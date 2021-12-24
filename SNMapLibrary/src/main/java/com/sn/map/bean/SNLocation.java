package com.sn.map.bean;

/**
 * 功能:位置 对象
 */
public class SNLocation {
    private long mTime = 0;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAltitude = 0.0;
    private float mAccuracy = 0.0f;
    private float mSpeed = 0.0f;
    private Object location;

    public SNLocation() {
    }

    public SNLocation(Object location, long mTime, double mLatitude, double mLongitude, double mAltitude, float mAccuracy, float mSpeed) {
        this.location = location;
        this.mTime = mTime;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAccuracy = mAccuracy;
        this.mSpeed = mSpeed;
    }

    public SNLocation(long mTime, double mLatitude, double mLongitude, float mAccuracy, float mSpeed) {
        this.mTime = mTime;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAccuracy = mAccuracy;
        this.mSpeed = mSpeed;
    }

    public SNLocation(double mLatitude, double mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    public <T> T getLocation() {
        return (T) location;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public float getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(float mAccuracy) {
        this.mAccuracy = mAccuracy;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }

    @Override
    public String toString()
    {
        return "SNLocation{" +
                "mTime=" + mTime +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mAltitude=" + mAltitude +
                ", mAccuracy=" + mAccuracy +
                ", mSpeed=" + mSpeed +
                ", location=" + location +
                '}';
    }
}
