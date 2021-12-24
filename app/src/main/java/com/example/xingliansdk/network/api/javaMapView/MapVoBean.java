package com.example.xingliansdk.network.api.javaMapView;

import java.util.List;

public class MapVoBean {

    private List<ListDTO> list;

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * walkDistance : 1
         * runDistance : 0
         * cyclingDistance : 0
         * second : 0
         * calorie : 0
         * date : 2021年07月
         * list : [{"myId":7,"type":1,"distance":1,"motionTime":"0:06:00","createTime":"07/01","calorie":0}]
         */

        private String walkDistance;
        private String runDistance;
        private String cyclingDistance;
        private int second;
        private int calorie;
        private String date;
        private List<ListDTO.ListChildDTO> list;

        public String getWalkDistance() {
            return walkDistance;
        }

        public void setWalkDistance(String walkDistance) {
            this.walkDistance = walkDistance;
        }

        public String getRunDistance() {
            return runDistance;
        }

        public void setRunDistance(String runDistance) {
            this.runDistance = runDistance;
        }

        public String getCyclingDistance() {
            return cyclingDistance;
        }

        public void setCyclingDistance(String cyclingDistance) {
            this.cyclingDistance = cyclingDistance;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<ListDTO.ListChildDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO.ListChildDTO> list) {
            this.list = list;
        }

        public static class ListChildDTO {
            /**
             * myId : 7
             * type : 1
             * distance : 1
             * motionTime : 0:06:00
             * createTime : 07/01
             * calorie : 0
             */

            private int myId;
            private int type;
            private String distance;
            private String motionTime;
            private String createTime;
            private String calorie;
            //心率
            private String heartRateData;
            //位置信息
            private String positionData;
            //步数
            private String steps;
            //平均配速
            private String avgPace;
            //平均速度
            private String avgSpeed;

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getDistance() {
                return distance;
            }

            public String getCalorie() {
                return calorie;
            }

            public void setCalorie(String calorie) {
                this.calorie = calorie;
            }

            public String getSteps() {
                return steps;
            }

            public void setSteps(String steps) {
                this.steps = steps;
            }

            public String getAvgPace() {
                return avgPace;
            }

            public void setAvgPace(String avgPace) {
                this.avgPace = avgPace;
            }

            public String getAvgSpeed() {
                return avgSpeed;
            }

            public void setAvgSpeed(String avgSpeed) {
                this.avgSpeed = avgSpeed;
            }

            public String getHeartRateData() {
                return heartRateData;
            }

            public void setHeartRateData(String heartRateData) {
                this.heartRateData = heartRateData;
            }

            public String getPositionData() {
                return positionData;
            }

            public void setPositionData(String positionData) {
                this.positionData = positionData;
            }

            public int getMyId() {
                return myId;
            }

            public void setMyId(int myId) {
                this.myId = myId;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }


            public String getMotionTime() {
                return motionTime;
            }

            public void setMotionTime(String motionTime) {
                this.motionTime = motionTime;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }
        }
    }
}
