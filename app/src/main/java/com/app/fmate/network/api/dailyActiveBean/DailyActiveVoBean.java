package com.app.fmate.network.api.dailyActiveBean;

import java.util.List;

public class DailyActiveVoBean {
    /**
     * totalSteps : 30
     * timeInterval : 60
     * avgSteps : 15
     * list : [{"date":"2021-08-10","totalSteps":20,"data":[{"steps":10,"distance":60,"calorie":10},{"steps":10,"distance":60,"calorie":10}]},{"date":"2021-08-04","totalSteps":10,"data":[{"steps":10,"distance":60,"calorie":10}]}]
     */
    private int totalSteps;
    private int timeInterval;
    private int avgSteps;
    private List<ListDTO> list;

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getAvgSteps() {
        return avgSteps;
    }

    public void setAvgSteps(int avgSteps) {
        this.avgSteps = avgSteps;
    }

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * date : 2021-08-10
         * totalSteps : 20
         * data : [{"steps":10,"distance":60,"calorie":10},{"steps":10,"distance":60,"calorie":10}]
         */

        private String date;
        private long totalSteps;
        private long startTimestamp;
        private long endTimestamp;
        private List<DataDTO> data;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getTotalSteps() {
            return totalSteps;
        }

        public void setTotalSteps(long totalSteps) {
            this.totalSteps = totalSteps;
        }

        public List<DataDTO> getData() {
            return data;
        }

        public void setData(List<DataDTO> data) {
            this.data = data;
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public long getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(long endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public static class DataDTO {
            /**
             * steps : 10
             * distance : 60.0
             * calorie : 10.0
             */

            private int steps;
            private double distance;
            private double calorie;

            public int getSteps() {
                return steps;
            }

            public void setSteps(int steps) {
                this.steps = steps;
            }

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public double getCalorie() {
                return calorie;
            }

            public void setCalorie(double calorie) {
                this.calorie = calorie;
            }
        }
    }
}
