package com.example.xingliansdk.network.api.bloodPressureView;

import java.util.List;

public class BloodPressureVoBean {

    private List<ListDTO> list;

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * createTime : 2021-08-04 16:16:15
         * stampCreateTime : 1628064975
         * systolicPressure : 150
         * diastolicPressure : 80
         */

        private String createTime;
        private long stampCreateTime;
        private int systolicPressure;
        private int diastolicPressure;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public long getStampCreateTime() {
            return stampCreateTime;
        }

        public void setStampCreateTime(long stampCreateTime) {
            this.stampCreateTime = stampCreateTime;
        }

        public int getSystolicPressure() {
            return systolicPressure;
        }

        public void setSystolicPressure(int systolicPressure) {
            this.systolicPressure = systolicPressure;
        }

        public int getDiastolicPressure() {
            return diastolicPressure;
        }

        public void setDiastolicPressure(int diastolicPressure) {
            this.diastolicPressure = diastolicPressure;
        }
    }
}
