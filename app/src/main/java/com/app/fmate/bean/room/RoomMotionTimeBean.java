package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.shon.connector.bean.TimeBean;

@Entity(tableName = "RoomMotionTimeBean")
public class RoomMotionTimeBean extends TimeBean {
    @PrimaryKey(autoGenerate = true)
    Integer  id ;
    String mList;
    //心率
    Boolean isHeart =false;
    //血氧
    Boolean isBloodOxygen=false;
    //运动
    Boolean isMotion=false;

    public Boolean getMotion() {
        return isMotion;
    }

    public void setMotion(Boolean motion) {
        isMotion = motion;
    }

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

    public RoomMotionTimeBean(){}
    public RoomMotionTimeBean(int dataUnitLength, int timeInterval, long startTime, long endTime){
        this.dataUnitType=dataUnitLength;
        this.timeInterval=timeInterval;
        this.startTime =startTime;
        this.endTime=endTime;
    }
    public RoomMotionTimeBean(Integer id, int dataUnitLength, int timeInterval, long startTime, long endTime){
        this.id=id;
        this.dataUnitType=dataUnitLength;
        this.timeInterval=timeInterval;
        this.startTime =startTime;
        this.endTime=endTime;
    }


}
