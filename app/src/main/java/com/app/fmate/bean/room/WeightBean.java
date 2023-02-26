package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "WeightBean")
public class WeightBean {
    @PrimaryKey
    long time;
    String array;
    Integer id;
    String  dateTime;
    String weight;
    String bmi;
    String value;//预留参数


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
    }



    public WeightBean() {
    }


    public WeightBean(
                      String array,
                      String  dateTime) {
        this.array=array;
        this.dateTime=dateTime;
    }
    public WeightBean(long time , String dateTime, String weight, String bmi ) {
        this.time = time;
        this.dateTime = dateTime;
        this.weight = weight;
        this.bmi = bmi;
    }
    public WeightBean(long time, String array, Integer id, String dateTime, String weight, String bmi, String value) {
        this.time = time;
        this.array = array;
        this.id = id;
        this.dateTime = dateTime;
        this.weight = weight;
        this.bmi = bmi;
        this.value = value;
    }

    public WeightBean(long time,  String dateTime, String weight, String bmi, String value) {
        this.time = time;
        this.dateTime = dateTime;
        this.weight = weight;
        this.bmi = bmi;
        this.value = value;
    }
}
