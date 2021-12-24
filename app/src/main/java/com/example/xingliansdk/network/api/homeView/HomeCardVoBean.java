package com.example.xingliansdk.network.api.homeView;

import java.io.Serializable;
import java.util.List;

public class HomeCardVoBean implements Serializable {

    /**
     * movingTarget : 10000
     * distance : 1.49
     * calorie : 0
     * list : [{"name":"心率","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/imag_xinlv.png","describe":"09/17","data":"82","startTime":1631808000,"endTime":1631859367,"type":1},{"name":"睡眠","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/img_sleep.png","describe":"09/16","data":"05小时46分钟","startTime":1631730419,"endTime":1631751179,"type":2},{"name":"压力","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/%E7%9F%A9%E5%BD%A2%E5%A4%87%E4%BB%BD%202.png","describe":"09/18","data":"-64","startTime":685123200,"endTime":685193760,"type":3},{"name":"血氧饱和度","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/imag_default_blood%20oxygen.png","describe":"检测血氧","data":"0","startTime":0,"endTime":0,"type":4},{"name":"血压","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/imag_default_blood%20pressure.png","describe":"记录你的血压","data":"","startTime":0,"endTime":0,"type":5},{"name":"体温","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/img_blood%20pressure(1).png","describe":"09/17","data":"36.9","startTime":1631808000,"endTime":1631859367,"type":6},{"name":"体重","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/imag_default_weight.png","describe":"09/17","data":"70.0","startTime":1631846084,"endTime":0,"type":7}]
     * steps : 2016
     */

    private String movingTarget;
    private String distance;
    private String calorie;
    private String steps;
    private List<ListDTO> list;

    public String getMovingTarget() {
        return movingTarget;
    }

    public void setMovingTarget(String movingTarget) {
        this.movingTarget = movingTarget;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO implements Serializable{
        /**
         * name : 心率
         * image : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ui-card/imag_xinlv.png
         * describe : 09/17
         * data : 82
         * startTime : 1631808000
         * endTime : 1631859367
         * type : 1
         */

        private String name;
        private String image;
        private String describe;
        private String data;
        private long startTime;
        private long endTime;
        private int type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
