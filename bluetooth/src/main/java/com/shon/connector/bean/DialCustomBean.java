package com.shon.connector.bean;

public class DialCustomBean {
    long uiFeature;
    long  binSize;
    int  color;
    int function;
    int location;
    int type;
    String name;

    public DialCustomBean() {
    }

    public DialCustomBean(int type,Integer uiFeature, Integer binSize, int color, int function, int location,  String name) {
        this.uiFeature = uiFeature;
        this.binSize = binSize;
        this.color = color;
        this.function = function;
        this.location = location;
        this.type = type;
        this.name = name;
    }
    public DialCustomBean(int type,Integer uiFeature, Integer binSize, int color, int function, int location ) {
        this.uiFeature = uiFeature;
        this.binSize = binSize;
        this.color = color;
        this.function = function;
        this.location = location;
        this.type = type;
    }

    public DialCustomBean(int type,Integer uiFeature, Integer binSize,String name ) {
        this.uiFeature = uiFeature;
        this.binSize = binSize;
        this.type = type;
        this.name = name;
    }
    public long getUiFeature() {
        return uiFeature;
    }

    public void setUiFeature(long uiFeature) {
        this.uiFeature = uiFeature;
    }

    public long getBinSize() {
        return binSize;
    }

    public void setBinSize(long binSize) {
        this.binSize = binSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
