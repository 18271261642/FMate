package com.app.fmate.bean.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "RoomExerciseBean")
public class RoomExerciseBean {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    public String distance = "0";
    public String calories = "0";
    public String averageSpeed = "0";
    public String pace = "0";
    public long time;
    public double type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getType() {
        return type;
    }

    public void setType(double type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    private long date;

    public RoomExerciseBean() {
    }

    public RoomExerciseBean(String distance, String calories, String averageSpeed, String pace, long time, double type, long date) {
        this.distance = distance;
        this.calories = calories;
        this.averageSpeed = averageSpeed;
        this.pace = pace;
        this.time = time;
        this.type = type;
        this.date = date;
    }
}
