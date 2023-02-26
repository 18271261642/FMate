package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.shon.connector.bean.TimeBean;

@Entity(tableName = "RoomSleepTimeBean")
public class RoomSleepTimeBean extends TimeBean {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    String mList;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
//    //心率
//    Boolean isHeart =false;
//    //血氧
//    Boolean isBloodOxygen=false;
//    public Boolean getHeart() {
//        return isHeart;
//    }
//
//    public void setHeart(Boolean isHeart) {
//        this.isHeart = isHeart;
//    }
//
//    public Boolean getBloodOxygen() {
//        return isBloodOxygen;
//    }
//
//    public void setBloodOxygen(Boolean bloodOxygen) {
//        isBloodOxygen = bloodOxygen;
//    }

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

    public RoomSleepTimeBean() {
    }

    public RoomSleepTimeBean(int dataUnitLength, int timeInterval, long startTime, long endTime, String dateTime) {
        this.dataUnitType = dataUnitLength;
        this.timeInterval = timeInterval;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dateTime = dateTime;
    }

    public RoomSleepTimeBean(Integer id, int dataUnitLength, int timeInterval, long startTime, long endTime) {
        this.id = id;
        this.dataUnitType = dataUnitLength;
        this.timeInterval = timeInterval;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "RoomSleepTimeBean{" +
                "id=" + id +
                ", mList='" + mList + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", mSwitch=" + mSwitch +
                ", specifiedTime=" + specifiedTime +
                ", specifiedTimeDescription='" + specifiedTimeDescription + '\'' +
                ", openHour=" + openHour +
                ", openMin=" + openMin +
                ", closeHour=" + closeHour +
                ", closeMin=" + closeMin +
                ", reminderInterval=" + reminderInterval +
                ", number=" + number +
                ", characteristic=" + characteristic +
                ", unicode='" + unicode + '\'' +
                ", unicodeType=" + unicodeType +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hours=" + hours +
                ", min=" + min +
                ", ss=" + ss +
                ", doNotDisturb=" + doNotDisturb +
                ", nightMode=" + nightMode +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", timeInterval=" + timeInterval +
                ", dataUnitType=" + dataUnitType +
                ", ReminderPeriod=" + ReminderPeriod +
                '}';
    }
}
