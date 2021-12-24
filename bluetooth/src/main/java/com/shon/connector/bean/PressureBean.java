package com.shon.connector.bean;

public class PressureBean {
    int fatigue;
    int stress;
    int heartHealth;

    @Override
    public String toString() {
        return "PressureBean{" +
                "fatigue=" + fatigue +
                ", stress=" + stress +
                ", heartHealth=" + heartHealth +
                '}';
    }

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public int getHeartHealth() {
        return heartHealth;
    }

    public void setHeartHealth(int heartHealth) {
        this.heartHealth = heartHealth;
    }

    public PressureBean() {
    }

    public PressureBean(int fatigue, int stress, int heartHealth) {
        this.fatigue = fatigue;
        this.stress = stress;
        this.heartHealth = heartHealth;
    }
}
