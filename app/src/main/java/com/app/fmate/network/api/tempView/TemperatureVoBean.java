package com.app.fmate.network.api.tempView;

import java.util.List;

public class TemperatureVoBean {

    private List<TemperatureVoListDTO> temperatureVoList;

    public List<TemperatureVoListDTO> getTemperatureVoList() {
        return temperatureVoList;
    }

    public void setTemperatureVoList(List<TemperatureVoListDTO> temperatureVoList) {
        this.temperatureVoList = temperatureVoList;
    }

    public static class TemperatureVoListDTO {
        /**
         * date : 2021-07-14 16:49:23
         * startTime : 2021-07-14 16:49:23
         * endTime : 2021-07-19 07:56:03
         * endTimestamp : 1626652563
         * startTimestamp : 1626252563
         * data : [80,90,100,120,130,76]
         * timeInterval : 10
         * max : 130
         * min : 76
         * avg : 99
         */

        private String date;
        private String startTime;
        private String endTime;
        private long endTimestamp;
        private long startTimestamp;
        private int timeInterval;
        private String max;
        private String min;
        private String avg;
        private List<Integer> data;

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

        public long getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(long endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public long getStartTimestamp() {
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

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getAvg() {
            return avg;
        }

        public void setAvg(String avg) {
            this.avg = avg;
        }

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }
    }
}
