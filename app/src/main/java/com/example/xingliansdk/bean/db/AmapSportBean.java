package com.example.xingliansdk.bean.db;



import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.io.Serializable;

/**
 * 用于保存GPS运动后的数据bean
 * Created by Admin
 * Date 2021/9/12
 */
@Entity(tableName = "AmapSportBean")
public class AmapSportBean  implements Serializable {

    //创建时间戳
    @PrimaryKey
    private long createTime;
    //手表Mac，保留
    private String deviceMac;
    //用户id
    private String userId;
    //日期 yyyy-MM-dd格式
    private String dayDate;
    //月份 yyyy-MM格式
    private String yearMonth;


    //类型 跑步、骑行等，int类型
    private int sportType;

    //地图类型，高德、google等，不同地图经纬度有差异 保留
    private int mapType;

    //运动时间 HH:mm:ss
    private String currentSportTime;
    //结束运动时的时间 long型，yyyy-MM-dd HH:mm
    private String endSportTime;
    //步数，保留
    private int currentSteps;
    //当前运动的距离
    private String distance;
    //卡路里
    private String calories;
    //平均速度
    private String averageSpeed;
    //平均配速
    private String pace;
    //心率集合，转字符串
    private String heartArrayStr;
    //经纬度集合，转字符串
    private String latLonArrayStr;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }



    public AmapSportBean() {
    }

    public AmapSportBean(String deviceMac, String userId, String dayDate, String yearMonth,
                         int sportType, int mapType, String currentSportTime, String endSportTime,
                         int currentSteps, String distance, String calories, String averageSpeed,
                         String pace, String heartArrayStr, String latLonArrayStr) {
        this.deviceMac = deviceMac;
        this.userId = userId;
        this.dayDate = dayDate;
        this.yearMonth = yearMonth;
        this.sportType = sportType;
        this.mapType = mapType;
        this.currentSportTime = currentSportTime;
        this.endSportTime = endSportTime;
        this.currentSteps = currentSteps;
        this.distance = distance;
        this.calories = calories;
        this.averageSpeed = averageSpeed;
        this.pace = pace;
        this.heartArrayStr = heartArrayStr;
        this.latLonArrayStr = latLonArrayStr;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }

    public String getCurrentSportTime() {
        return currentSportTime;
    }

    public void setCurrentSportTime(String currentSportTime) {
        this.currentSportTime = currentSportTime;
    }

    public String getEndSportTime() {
        return endSportTime;
    }

    public void setEndSportTime(String endSportTime) {
        this.endSportTime = endSportTime;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public void setCurrentSteps(int currentSteps) {
        this.currentSteps = currentSteps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getHeartArrayStr() {
        return heartArrayStr;
    }

    public void setHeartArrayStr(String heartArrayStr) {
        this.heartArrayStr = heartArrayStr;
    }

    public String getLatLonArrayStr() {
        return latLonArrayStr;
    }

    public void setLatLonArrayStr(String latLonArrayStr) {
        this.latLonArrayStr = latLonArrayStr;
    }


    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public String toString() {
        return "AmapSportBean{" +
                "deviceMac='" + deviceMac + '\'' +
                ", userId='" + userId + '\'' +
                ", dayDate='" + dayDate + '\'' +
                ", yearMonth='" + yearMonth + '\'' +
                ", sportType=" + sportType +
                ", mapType=" + mapType +
                ", currentSportTime='" + currentSportTime + '\'' +
                ", endSportTime=" + endSportTime +
                ", currentSteps=" + currentSteps +
                ", distance='" + distance + '\'' +
                ", calories='" + calories + '\'' +
                ", averageSpeed='" + averageSpeed + '\'' +
                ", pace='" + pace + '\'' +
                ", heartArrayStr='" + heartArrayStr + '\'' +
                ", latLonArrayStr='" + latLonArrayStr + '\'' +
                '}';
    }
}
