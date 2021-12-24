package com.example.xingliansdk.network.api.weightView;

import java.util.List;

public class WeightModeBean {

    /**
     * weight : 71.7
     * bmi : 24.3
     * lastWeight : 75.7
     * changeWeight : -4.0
     * weightModelList : [{"weight":70,"createTime":"2021-07-18 10:40:11"},{"weight":60,"createTime":"2021-07-17 10:40:11"},{"weight":65.6,"createTime":"2021-07-16 16:33:45"},{"weight":60,"createTime":"2021-07-16 16:31:42"},{"weight":90,"createTime":"2021-07-16 10:40:11"},{"weight":90,"createTime":"2021-07-15 18:35:15"},{"weight":60,"createTime":"2021-07-14 18:35:15"},{"weight":70,"createTime":"2021-07-13 18:35:15"},{"weight":80,"createTime":"2021-07-12 18:35:15"}]
     */

    private String weight;
    private String bmi;
    private String lastWeight;
    private String changeWeight;
    private List<WeightModelListDTO> weightModelList;

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

    public String getLastWeight() {
        return lastWeight;
    }

    public void setLastWeight(String lastWeight) {
        this.lastWeight = lastWeight;
    }

    public String getChangeWeight() {
        return changeWeight;
    }

    public void setChangeWeight(String changeWeight) {
        this.changeWeight = changeWeight;
    }

    public List<WeightModelListDTO> getWeightModelList() {
        return weightModelList;
    }

    public void setWeightModelList(List<WeightModelListDTO> weightModelList) {
        this.weightModelList = weightModelList;
    }

    public static class WeightModelListDTO {
        /**
         * weight : 70.0
         * createTime : 2021-07-18 10:40:11
         */

        private String weight;
        private String createTime;
        private String bmi;
        private long stampCreateTime;

        public String getBmi() {
            return bmi;
        }

        public void setBmi(String bmi) {
            this.bmi = bmi;
        }

        public long getStampCreateTime() {
            return stampCreateTime;
        }

        public void setStampCreateTime(long stampCreateTime) {
            this.stampCreateTime = stampCreateTime;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
