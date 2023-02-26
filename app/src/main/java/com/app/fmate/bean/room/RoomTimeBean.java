package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.shon.connector.bean.TimeBean;

@Entity(tableName = "RoomTimeBean")
public class RoomTimeBean extends TimeBean {
    @PrimaryKey(autoGenerate = true)
    Integer  id ;
    String mList;
    //心率
    Boolean isHeart =false;
    //血氧
    Boolean isBloodOxygen=false;
    public Boolean getHeart() {
        return isHeart;
    }

    public void setHeart(Boolean isHeart) {
        this.isHeart = isHeart;
    }

    public Boolean getBloodOxygen() {
        return isBloodOxygen;
    }

    public void setBloodOxygen(Boolean bloodOxygen) {
        isBloodOxygen = bloodOxygen;
    }

    public String getmList() {
        return mList;
    }

    public void setmList(String mList) {
        this.mList = mList;
    }

    //    List<Integer> mList;

//    public List<Integer> getList() {
//        return mList;
//    }
//
//    public void setList(List<Integer> mList) {
//        this.mList = mList;
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoomTimeBean(){}
    public RoomTimeBean(int dataUnitLength,int timeInterval,long startTime,long endTime, boolean isHeart, boolean isBloodOxygen){
        this.dataUnitType=dataUnitLength;
        this.timeInterval=timeInterval;
        this.startTime =startTime;
        this.endTime=endTime;
        this.isHeart=isHeart;
        this.isBloodOxygen=isBloodOxygen;
    }
    public RoomTimeBean(Integer id,int dataUnitLength,int timeInterval,long startTime,long endTime, boolean isHeart, boolean isBloodOxygen){
        this.id=id;
        this.dataUnitType=dataUnitLength;
        this.timeInterval=timeInterval;
        this.startTime =startTime;
        this.endTime=endTime;
        this.isHeart=isHeart;
        this.isBloodOxygen=isBloodOxygen;
    }


}
