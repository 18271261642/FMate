package com.app.fmate.bean;

public class Bean {
    public int type;
    public int distance;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    public Bean() {
    }
    public Bean(int type, int distance) {
        this.type = type;
        this.distance = distance;
    }
}
