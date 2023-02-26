package com.app.fmate.network.api.bloodOxygen;

import java.util.List;

public class BloodOxygenVoBean {

    private List<BloodOxygenVoListDTO> bloodOxygenVoList;

    public List<BloodOxygenVoListDTO> getBloodOxygenVoList() {
        return bloodOxygenVoList;
    }

    public void setBloodOxygenVoList(List<BloodOxygenVoListDTO> bloodOxygenVoList) {
        this.bloodOxygenVoList = bloodOxygenVoList;
    }

    public static class BloodOxygenVoListDTO {
        /**
         * date : 2021-07-23
         * startTime : 2021-07-23 00:00:00
         * endTime : 2021-07-23 16:03:17
         * endTimestamp : 1627027397
         * startTimestamp : 1626969600
         * bloodOxygen : [0,0,0,0,0,0,0,0,0]
         * timeInterval : 60
         * max : 92
         * min : 72
         * avg : 82
         */

        private String date;
        private String startTime;
        private String endTime;
        private int endTimestamp;
        private int startTimestamp;
        private int timeInterval;
        private int max;
        private int min;
        private int avg;
        private List<Integer> bloodOxygen;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(int endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public int getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(int startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public int getTimeInterval() {
            return timeInterval;
        }

        public void setTimeInterval(int timeInterval) {
            this.timeInterval = timeInterval;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getAvg() {
            return avg;
        }

        public void setAvg(int avg) {
            this.avg = avg;
        }

        public List<Integer> getBloodOxygen() {
            return bloodOxygen;
        }

        public void setBloodOxygen(List<Integer> bloodOxygen) {
            this.bloodOxygen = bloodOxygen;
        }
    }
}
