package com.example.xingliansdk.ui.bp;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

public class PPG1CacheDb extends LitePalSupport implements Serializable {

    private String userId;

    private String deviceMac;

    //yyyy-MM-dd格式
    private String dayStr;

    //对应的时间戳，作为条件
    private String ppgTimeStr;

    //状态，是否已经上传， String 类型 0,1,2
    private String dbStatus;



    private List<Integer> bbpDataList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public String getPpgTimeStr() {
        return ppgTimeStr;
    }

    public void setPpgTimeStr(String ppgTimeStr) {
        this.ppgTimeStr = ppgTimeStr;
    }



    public String getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus) {
        this.dbStatus = dbStatus;
    }

    public List<Integer> getBbpDataList() {
        return bbpDataList;
    }

    public void setBbpDataList(List<Integer> bbpDataList) {
        this.bbpDataList = bbpDataList;
    }

    @Override
    public String toString() {
        return "PPG1CacheDb{" +
                "userId='" + userId + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", ppgTimeStr='" + ppgTimeStr + '\'' +
                ", dbStatus='" + dbStatus + '\'' +
                ", bbpDataList=" + bbpDataList +
                '}';
    }


    public String getData(){
        return "PPG1CacheDb{" +
                "userId='" + userId + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", ppgTimeStr='" + ppgTimeStr + '\'' +
                ", dbStatus='" + dbStatus + '\'' +
                ", bbpDataList=" + bbpDataList.size() +
                '}';
    }
}
