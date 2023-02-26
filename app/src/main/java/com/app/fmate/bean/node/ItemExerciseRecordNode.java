package com.app.fmate.bean.node;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;
@Entity(tableName = "ItemExerciseRecordNode")
public class ItemExerciseRecordNode extends BaseNode {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    private String distance  ;
    private String calories  ;
    private String averageSpeed  ;
    private String pace ;
    private long time; //这个是时间秒数
    private double type;
    private long date; //这个是保存时的当前时间
    private String dateTime;//这个是String 类型的

    public ItemExerciseRecordNode()
    {}

    public ItemExerciseRecordNode(String distance, String calories, String averageSpeed, String pace, long time, double type, long date) {
        this.distance = distance;
        this.calories = calories;
        this.averageSpeed = averageSpeed;
        this.pace = pace;
        this.time = time;
        this.type = type;
        this.date = date;
    }

    public ItemExerciseRecordNode(String distance, String calories, String averageSpeed, String pace, long time, double type, long date, String dateTime) {
        this.distance = distance;
        this.calories = calories;
        this.averageSpeed = averageSpeed;
        this.pace = pace;
        this.time = time;
        this.type = type;
        this.date = date;
        this.dateTime = dateTime;
    }

    public ItemExerciseRecordNode(double type, String distance) {
        this.distance = distance;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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



    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
