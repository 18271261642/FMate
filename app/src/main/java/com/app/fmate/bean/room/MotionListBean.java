package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MotionListBean")
public class MotionListBean {
    public static long day = 86400;
    @PrimaryKey
    long startTime;
    long endTime;
    String StepList;
    long totalSteps;
    Boolean isAllDay = false;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * 类型 比如心率为type=1 ,type=2为血氧
     */
    int type;
    public long getTotalSteps() {
        return totalSteps;
    }
    public void setTotalSteps(long totalSteps) {
        this.totalSteps = totalSteps;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean AllDay) {
        isAllDay = AllDay;
    }

    public String getStepList() {
        return StepList;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStepList(String heart) {
        this.StepList = heart;
    }

    public MotionListBean() {
    }

    public MotionListBean(long startTime, long endTime, String stepList, long totalSteps, Boolean isAllDay, String dateTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        StepList = stepList;
        this.totalSteps = totalSteps;
        this.isAllDay = isAllDay;
        this.dateTime = dateTime;
        if (endTime - startTime >= day) {
            this.isAllDay = isAllDay;
        }
    }


    @Override
    public String toString() {
        return "MotionListBean{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", StepList='" + StepList + '\'' +
                ", totalSteps=" + totalSteps +
                ", isAllDay=" + isAllDay +
                ", dateTime='" + dateTime + '\'' +
                ", type=" + type +
                '}';
    }
}
