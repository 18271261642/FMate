package com.app.fmate.network.api.weightView;

public class SetWeightBean {

    /**
     * id : 996.0
     * userId : 620dhe9358po
     * weight : 48.1
     * bmi : 18.8
     * date : 2021-07-27
     * createTime : 07-27 16:11
     */
    private double id;
    private String userId;
    private String weight;
    private String bmi;
    private String date;
    private String createTime;

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SetWeightBean{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", weight='" + weight + '\'' +
                ", bmi='" + bmi + '\'' +
                ", date='" + date + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
