package com.app.fmate.bean;

/**
 * 这个flash的bean类
 * 因为可能 用监听的话无法满足部分条件所以现在在转使用eventbus
 */
public class FlashBean {
    int currentProgress;
    int maxProgress;
    int type;
    int position;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;
    public FlashBean() {
    }


    public FlashBean(int currentProgress, int maxProgress, int position,int id) {
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
        this.position = position;
        this.id=id;
    }

    public FlashBean(int currentProgress, int maxProgress, int position) {
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
        this.position = position;
    }

    public FlashBean(int currentProgress, int maxProgress) {
        this.currentProgress = currentProgress;
        this.maxProgress = maxProgress;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FlashBean{" +
                "currentProgress=" + currentProgress +
                ", maxProgress=" + maxProgress +
                ", type=" + type +
                ", id=" + id +
                '}';
    }
}
