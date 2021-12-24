package com.shon.connector.bean;

import java.util.List;

public class SleepBean  {
    public static  final  int DEFAULT=0;
    /**
     * 深睡
     */
    public static  final  int DEEP_SLEEP=1;
    /**
     *浅睡
     */
    public static  final  int LIGHT_SLEEP=2;
    /**
     *动眼
     */
    public static  final  int EYE_MOVEMENT=3;
    /**
     *清醒
     */
    public static  final  int WIDE_AWAKE=4;


    /**
     * 开始时间戳
     */
    long startTime;
    /**
     * 结束时间戳
     */
    long endTime;
    /**
     * 索引1
     */
    int indexOne;
    /**
     * 长度1
     */
    int lengthOne;
    /**
     * 呼吸暂停现象总时间
     */
    int totalApneaTime;
    /**
     * 呼吸暂停现象次数
     */
    int NumberOfApnea;
    /**
     * 平均心率
     */
    int AverageHeartRate;
    /**
     * 最大心率
     */
    int MaximumHeartRate;
    /**
     * 最小心率
     */
    int MinimumHeartRate;
    /**
     * 索引2
     */
    int indexTwo;
    /**
     * 长度2
     */
    int lengthTwo;
    /**
     * 呼吸质量
     */
    int respiratoryQuality;
    /**
     * 睡眠数据
     */
    List<StepChildBean> mList;
    /**
     * 睡眠数据转成String 类型
     */
    String StepChildList;

    public int getRespiratoryQuality() {
        return respiratoryQuality;
    }

    public void setRespiratoryQuality(int respiratoryQuality) {
        this.respiratoryQuality = respiratoryQuality;
    }

    public String getStepChildList() {
        return StepChildList;
    }

    public void setStepChildList(String stepChildList) {
        StepChildList = stepChildList;
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

    public int getIndexOne() {
        return indexOne;
    }

    public void setIndexOne(int indexOne) {
        this.indexOne = indexOne;
    }

    public int getLengthOne() {
        return lengthOne;
    }

    public void setLengthOne(int lengthOne) {
        this.lengthOne = lengthOne;
    }

    public int getTotalApneaTime() {
        return totalApneaTime;
    }

    public void setTotalApneaTime(int totalApneaTime) {
        this.totalApneaTime = totalApneaTime;
    }

    public int getNumberOfApnea() {
        return NumberOfApnea;
    }

    public void setNumberOfApnea(int numberOfApnea) {
        NumberOfApnea = numberOfApnea;
    }

    public int getAverageHeartRate() {
        return AverageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        AverageHeartRate = averageHeartRate;
    }

    public int getMaximumHeartRate() {
        return MaximumHeartRate;
    }

    public void setMaximumHeartRate(int maximumHeartRate) {
        MaximumHeartRate = maximumHeartRate;
    }

    public int getMinimumHeartRate() {
        return MinimumHeartRate;
    }

    public void setMinimumHeartRate(int minimumHeartRate) {
        MinimumHeartRate = minimumHeartRate;
    }

    public int getIndexTwo() {
        return indexTwo;
    }

    public void setIndexTwo(int indexTwo) {
        this.indexTwo = indexTwo;
    }

    public int getLengthTwo() {
        return lengthTwo;
    }

    public void setLengthTwo(int lengthTwo) {
        this.lengthTwo = lengthTwo;
    }

    public List<StepChildBean> getmList() {
        return mList;
    }

    public void setmList(List<StepChildBean> mList) {
        this.mList = mList;
    }

      public static class StepChildBean {
        /**
         * 睡眠类型
         * 0默认  1深睡
         * 2浅睡,3眼动
         * 4清醒
         */
        int type = DEFAULT;
        /**
         * 睡眠时间 单位
         * min
         */
        int duration = DEFAULT;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
