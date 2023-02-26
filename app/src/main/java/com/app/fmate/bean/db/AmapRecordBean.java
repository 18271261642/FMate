package com.app.fmate.bean.db;

import java.util.List;

public class AmapRecordBean {
    //某个月
    private String monthStr;
    //细分数组
    private List<AmapSportBean> list;
    //距离
    private String distanceCount;
    //卡路里
    private String caloriesCount;
    //次数
    private int sportCount;
    //是否展示
    private boolean isShow;
    //步行距离
    private String walkDistance;
    //跑步距离
    private String runDistance;
    //骑行距离
    private String rideDistance;

    public String getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(String walkDistance) {
        this.walkDistance = walkDistance;
    }

    public String getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(String runDistance) {
        this.runDistance = runDistance;
    }

    public String getRideDistance() {
        return rideDistance;
    }

    public void setRideDistance(String rideDistance) {
        this.rideDistance = rideDistance;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public List<AmapSportBean> getList() {
        return list;
    }

    public void setList(List<AmapSportBean> list) {
        this.list = list;
    }

    public String getDistanceCount() {
        return distanceCount;
    }

    public void setDistanceCount(String distanceCount) {
        this.distanceCount = distanceCount;
    }

    public String getCaloriesCount() {
        return caloriesCount;
    }

    public void setCaloriesCount(String caloriesCount) {
        this.caloriesCount = caloriesCount;
    }

    public int getSportCount() {
        return sportCount;
    }

    public void setSportCount(int sportCount) {
        this.sportCount = sportCount;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
