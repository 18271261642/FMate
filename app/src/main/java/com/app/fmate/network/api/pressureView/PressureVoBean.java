package com.app.fmate.network.api.pressureView;

import java.util.List;

public class PressureVoBean {

    private List<PressureVoListDTO> pressureVoList;

    public List<PressureVoListDTO> getPressureVoList() {
        return pressureVoList;
    }

    public void setPressureVoList(List<PressureVoListDTO> pressureVoList) {
        this.pressureVoList = pressureVoList;
    }

    public static class PressureVoListDTO {
        /**
         * date : 2021-07-14
         * startTime : 2021-07-14 16:49:23
         * endTime : 2021-07-19 07:56:03
         * endTimestamp : 1626652563
         * startTimestamp : 1626252563
         * data : [{"fatigue":1,"stress":1,"heartHealth":1}]
         * timeInterval : 10
         * max : 306
         * min : 86
         * avg : 9999
         */

        private String date;
        private String startTime;
        private String endTime;
        private long endTimestamp;
        private long startTimestamp;
        private int timeInterval;

        private int stressMax;
        private int stressMin;
        private int stressAvg;
        private int fatigueMax;
        private int fatigueMin;
        private int fatigueAvg;
        private int heartHealthMax;
        private int heartHealthMin;
        private int heartHealthAvg;

        public int getStressMax() {
            return stressMax;
        }

        public void setStressMax(int stressMax) {
            this.stressMax = stressMax;
        }

        public int getStressMin() {
            return stressMin;
        }

        public void setStressMin(int stressMin) {
            this.stressMin = stressMin;
        }

        public int getStressAvg() {
            return stressAvg;
        }

        public void setStressAvg(int stressAvg) {
            this.stressAvg = stressAvg;
        }

        public int getFatigueMax() {
            return fatigueMax;
        }

        public void setFatigueMax(int fatigueMax) {
            this.fatigueMax = fatigueMax;
        }

        public int getFatigueMin() {
            return fatigueMin;
        }

        public void setFatigueMin(int fatigueMin) {
            this.fatigueMin = fatigueMin;
        }

        public int getFatigueAvg() {
            return fatigueAvg;
        }

        public void setFatigueAvg(int fatigueAvg) {
            this.fatigueAvg = fatigueAvg;
        }

        public int getHeartHealthMax() {
            return heartHealthMax;
        }

        public void setHeartHealthMax(int heartHealthMax) {
            this.heartHealthMax = heartHealthMax;
        }

        public int getHeartHealthMin() {
            return heartHealthMin;
        }

        public void setHeartHealthMin(int heartHealthMin) {
            this.heartHealthMin = heartHealthMin;
        }

        public int getHeartHealthAvg() {
            return heartHealthAvg;
        }

        public void setHeartHealthAvg(int heartHealthAvg) {
            this.heartHealthAvg = heartHealthAvg;
        }

        private List<DataDTO> data;

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

        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public int getTimeInterval() {
            return timeInterval;
        }

        public void setTimeInterval(int timeInterval) {
            this.timeInterval = timeInterval;
        }

        public List<DataDTO> getData() {
            return data;
        }

        public void setData(List<DataDTO> data) {
            this.data = data;
        }

        public static class DataDTO {
            /**
             * fatigue : 1
             * stress : 1
             * heartHealth : 1
             */

            private int fatigue;
            private int stress;
            private int heartHealth;

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
        }
    }
}
