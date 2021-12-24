package com.example.xingliansdk.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "CustomizeDialBean")
public class CustomizeDialBean {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    long startTime;
    String date;
    String imgPath;
    int color;
    int  functionType;
    int locationType;
    String  function;
    String uiFeature;
    String xAxis; //用于长按显示删除按钮
    String yAxis;//暂时无用
    String value;
    public CustomizeDialBean()
    {}
    public CustomizeDialBean(long startTime, String date, String imgPath, int color, int functionType, int locationType, String function, String uiFeature) {
        this.startTime = startTime;
        this.date = date;
        this.imgPath = imgPath;
        this.color = color;
        this.functionType = functionType;
        this.locationType = locationType;
        this.function = function;
        this.uiFeature = uiFeature;
    }

    public CustomizeDialBean(int id, long startTime, String date, String imgPath, int color, int functionType, int locationType, String function, String uiFeature, String xAxis, String yAxis, String value) {
        this.id = id;
        this.startTime = startTime;
        this.date = date;
        this.imgPath = imgPath;
        this.color = color;
        this.functionType = functionType;
        this.locationType = locationType;
        this.function = function;
        this.uiFeature = uiFeature;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getFunctionType() {
        return functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getUiFeature() {
        return uiFeature;
    }

    public void setUiFeature(String uiFeature) {
        this.uiFeature = uiFeature;
    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
