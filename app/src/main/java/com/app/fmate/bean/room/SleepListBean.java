package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "SleepListBean")
public class SleepListBean {
    @PrimaryKey
    long startTime;
//    long startTimeZero;
    String  dateTime;
    int AverageHeartRate;
    int MaximumHeartRate;
    int MinimumHeartRate;
    int NumberOfApnea;

    long endTime;
    int indexOne;
    int indexTwo;
    int lengthOne;
    int lengthTwo;
    int totalApneaTime;
    //直接弄成String类型的
    String sleepList;
    Integer id;

    int setRespiratoryQuality;

//    public long getStartTimeZero() {
//        return startTimeZero;
//    }
//
//    public void setStartTimeZero(long startTimeZero) {
//        this.startTimeZero = startTimeZero;
//    }

    public int getSetRespiratoryQuality() {
        return setRespiratoryQuality;
    }

    public void setSetRespiratoryQuality(int setRespiratoryQuality) {
        this.setRespiratoryQuality = setRespiratoryQuality;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSleepList() {
        return sleepList;
    }

    public void setSleepList(String sleepList) {
        this.sleepList = sleepList;
    }

    public int getAverageHeartRate() {
        return AverageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        AverageHeartRate = averageHeartRate;
    }

    public int getMaximumHeartRate() {
        return MaximumHeartRate;
    }

    public void setMaximumHeartRate(int maximumHeartRate) {
        MaximumHeartRate = maximumHeartRate;
    }

    public int getMinimumHeartRate() {
        return MinimumHeartRate;
    }

    public void setMinimumHeartRate(int minimumHeartRate) {
        MinimumHeartRate = minimumHeartRate;
    }

    public int getNumberOfApnea() {
        return NumberOfApnea;
    }

    public void setNumberOfApnea(int numberOfApnea) {
        NumberOfApnea = numberOfApnea;
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

    public int getIndexOne() {
        return indexOne;
    }

    public void setIndexOne(int indexOne) {
        this.indexOne = indexOne;
    }

    public int getIndexTwo() {
        return indexTwo;
    }

    public void setIndexTwo(int indexTwo) {
        this.indexTwo = indexTwo;
    }

    public int getLengthOne() {
        return lengthOne;
    }

    public void setLengthOne(int lengthOne) {
        this.lengthOne = lengthOne;
    }

    public int getLengthTwo() {
        return lengthTwo;
    }

    public void setLengthTwo(int lengthTwo) {
        this.lengthTwo = lengthTwo;
    }

    public int getTotalApneaTime() {
        return totalApneaTime;
    }

    public void setTotalApneaTime(int totalApneaTime) {
        this.totalApneaTime = totalApneaTime;
    }

    public SleepListBean() {
    }

//    public SleepListBean(long startTime, long startTimeZero, String dateTime, int averageHeartRate, int maximumHeartRate, int minimumHeartRate, int numberOfApnea, long endTime, int indexOne, int indexTwo, int lengthOne, int lengthTwo, int totalApneaTime, String sleepList, Integer id, int setRespiratoryQuality) {
//        this.startTime = startTime;
//        this.startTimeZero = startTimeZero;
//        this.dateTime = dateTime;
//        AverageHeartRate = averageHeartRate;
//        MaximumHeartRate = maximumHeartRate;
//        MinimumHeartRate = minimumHeartRate;
//        NumberOfApnea = numberOfApnea;
//        this.endTime = endTime;
//        this.indexOne = indexOne;
//        this.indexTwo = indexTwo;
//        this.lengthOne = lengthOne;
//        this.lengthTwo = lengthTwo;
//        this.totalApneaTime = totalApneaTime;
//        this.sleepList = sleepList;
//        this.id = id;
//        this.setRespiratoryQuality = setRespiratoryQuality;
//    }

    public SleepListBean(long startTime,
                         int AverageHeartRate,
                         int MaximumHeartRate,
                         int MinimumHeartRate,
                         int NumberOfApnea,
                         long endTime,
                         int indexOne,
                         int indexTwo,
                         int lengthOne,
                         int lengthTwo,
                         int totalApneaTime,
                         int setRespiratoryQuality,
                         String sleepList,
                         String  dateTime) {
        this.startTime=startTime;
        this.AverageHeartRate=AverageHeartRate;
        this.MaximumHeartRate=MaximumHeartRate;
        this.MinimumHeartRate=MinimumHeartRate;
        this.NumberOfApnea=NumberOfApnea;
        this.endTime=endTime;
        this.indexOne=indexOne;
        this.indexTwo=indexTwo;
        this.lengthOne=lengthOne;
        this.lengthTwo=lengthTwo;
        this.totalApneaTime=totalApneaTime;
        this.setRespiratoryQuality=setRespiratoryQuality;
        this.sleepList=sleepList;
        this.dateTime=dateTime;
    }

    public SleepListBean(long startTime,
                         int AverageHeartRate,
                         int MaximumHeartRate,
                         int MinimumHeartRate,
                         int NumberOfApnea,
                         long endTime,
                         int totalApneaTime,
                         int setRespiratoryQuality,
                         String sleepList,
                         String  dateTime) {
        this.startTime=startTime;
        this.AverageHeartRate=AverageHeartRate;
        this.MaximumHeartRate=MaximumHeartRate;
        this.MinimumHeartRate=MinimumHeartRate;
        this.NumberOfApnea=NumberOfApnea;
        this.endTime=endTime;
        this.totalApneaTime=totalApneaTime;
        this.setRespiratoryQuality=setRespiratoryQuality;
        this.sleepList=sleepList;
        this.dateTime=dateTime;
    }

}
