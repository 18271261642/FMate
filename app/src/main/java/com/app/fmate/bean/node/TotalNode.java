package com.app.fmate.bean.node;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TotalNode extends BaseNode {
    private String distance;
    private String calories;
    private String averageSpeed;
    private String pace;
    private double type;

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

    public double getType() {
        return type;
    }

    public void setType(double type) {
        this.type = type;
    }


    public TotalNode()
    {}
    public TotalNode(String distance, String calories, String averageSpeed, String pace, double type) {
        this.distance = distance;
        this.calories = calories;
        this.averageSpeed = averageSpeed;
        this.pace = pace;
        this.type = type;
    }
    public TotalNode(String distance, double type) {
        this.distance = distance;
        this.type = type;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
