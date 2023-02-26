package com.app.fmate.bean.dialBean;

public class CustomizeColorBean {
    String color;
    int type;
    boolean colorCheck=false;

    public boolean isColorCheck() {
        return colorCheck;
    }

    public void setColorCheck(boolean colorCheck) {
        this.colorCheck = colorCheck;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public CustomizeColorBean(){

    }
    public CustomizeColorBean(String color, int type) {
        this.color = color;
        this.type = type;
    }

    public CustomizeColorBean(String color, int type, boolean colorCheck) {
        this.color = color;
        this.type = type;
        this.colorCheck = colorCheck;
    }
}
