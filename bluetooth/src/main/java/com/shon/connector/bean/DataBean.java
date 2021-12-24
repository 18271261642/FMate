package com.shon.connector.bean;

import android.text.TextUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import kotlin.annotation.AnnotationRetention;

/**
 * 此实体类多方共用
 */
public class DataBean {
    /**
     * 天气特征值
     */
    public static byte TEMPERATURE_FEATURES = 0x01;
    /**
     * 天气unicode编码
     */
    public static byte TEMPERATURE_FEATURES_UNICODE = 0x31;

    /**
     * 心率
     */
    int heartRate;
    /**
     * 心率1
     */
    int heartRate1;
    /**
     * 血氧
     */
    int bloodOxygen;
    /**
     * 血压
     */
    int bloodPressure;
    /**
     * 温度
     */
    String temperature;
    /**
     * 天气类型
     */
    int weatherType; //只想含有1-5和 ff
//    public static final   int Status_OK = 1;
//    public static final   int Status_Fail = 2;
//    public static final   int Status_Empty = 3;
//    public static final    int Status_End = 4;
//    @IntDef(flag = true,value = {Status_OK, Status_Fail, Status_Empty, Status_End})
//    @Retention(AnnotationRetention.SOURCE)

    /**
     * 活动
     */
    int activity;
    /**
     * 计步
     */
    int pedometer;
    /**
     * 睡眠
     */
    int sleep;
    /**
     * 总计步数
     */
    long totalSteps=0;
    /**
     * 距离
     */
    long distance;
    /**
     * 卡路里
     */
    long Calories;
    /**
     * 平均心率
     */
    long averageHeartRate;
    /**
     * 最小心率
     */
    long minHeartRate;
    /**
     * 最大心率
     */
    long MaxHeartRate;
    /**
     * 舒张压
     */
    int systolicBloodPressure;
    /**
     * 收缩压
     */
    int diastolicBloodPressure;
    /**
     * 湿度
     */
    String humidity;
    /**
     * 类型
     */
    int type;

    /**
     * 当天最高温度
     */
    String highestTemperatureToday;
    /**
     * 当天最低温度
     */
    String lowestTemperatureToday;
    /**
     * 空气质量指数
     */
    String airQuality;
    /**
     * 紫外线指数
     */
    String UVIndex;
    /**
     * 日出时
     */
    int sunriseHours;
    /**
     * 日出分
     */
    int sunriseMin;
    /**
     * 日落时
     */
    int sunsetHours;
    /**
     * 日落分
     */
    int sunsetMin;
    String characteristic;
    byte unicodeType;
    String unicodeTitle;
    /**
     * 时间戳
     */
    long time;
    /**
     * 心脏健康指数
     */
    int heartHealth;
    /**
     * 压力度
     */
    int stress;
    /**
     * 疲劳值
     */
    int fatigue;
    /**
     * 应激起始时间戳
     */
    int stressStartTime;
    /**
     * 应激结束时间戳
     */
    int stressEndTime;
    /**
     * 应激时长类型
     */
    int stressTimeType;
    /**
     * 应激效果类型
     */
    int stressResultType;
    /**
     * 心率测量状态
     */
    int HeartRateMeasureType;
    /**
     * 血氧测量状态
     */
    int BloodOxygenMeasureType;
    /**
     * 血压测量状态
     */
    int bloodPressureMeasureType;
    /**
     * 温度测量状态
     */
    int temperatureMeasureType;
    /**
     * 湿度测量状态
     */
    int humidityMeasureType;
    /**
     * 开始时针
     */
    int openHour;
    /**
     * 开始分针
     */
    int openMin;
    /**
     * 关闭时针
     */
    int closeHour;
    /**
     * 关闭分针
     */
    int closeMin;
    /**
     * 指定时间测量开关
     */
    int  timeMeasurementSwitch;

    public int getHeartRate1() {
        return heartRate1;
    }

    public void setHeartRate1(int heartRate1) {
        this.heartRate1 = heartRate1;
    }

    public int getTimeMeasurementSwitch() {
        return timeMeasurementSwitch;
    }

    public void setTimeMeasurementSwitch(int timeMeasurementSwitch) {
        this.timeMeasurementSwitch = timeMeasurementSwitch;
    }

    public int getOpenHour() {
        return openHour;
    }

    public void setOpenHour(int openHour) {
        this.openHour = openHour;
    }

    public int getOpenMin() {
        return openMin;
    }

    public void setOpenMin(int openMin) {
        this.openMin = openMin;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    public int getCloseMin() {
        return closeMin;
    }

    public void setCloseMin(int closeMin) {
        this.closeMin = closeMin;
    }

    public int getHeartRateMeasureType() {
        return HeartRateMeasureType;
    }

    public void setHeartRateMeasureType(int heartRateMeasureType) {
        HeartRateMeasureType = heartRateMeasureType;
    }

    public int getBloodOxygenMeasureType() {
        return BloodOxygenMeasureType;
    }

    public void setBloodOxygenMeasureType(int bloodOxygenMeasureType) {
        BloodOxygenMeasureType = bloodOxygenMeasureType;
    }

    public int getBloodPressureMeasureType() {
        return bloodPressureMeasureType;
    }

    public void setBloodPressureMeasureType(int bloodPressureMeasureType) {
        this.bloodPressureMeasureType = bloodPressureMeasureType;
    }

    public int getTemperatureMeasureType() {
        return temperatureMeasureType;
    }

    public void setTemperatureMeasureType(int temperatureMeasureType) {
        this.temperatureMeasureType = temperatureMeasureType;
    }

    public int getHumidityMeasureType() {
        return humidityMeasureType;
    }

    public void setHumidityMeasureType(int humidityMeasureType) {
        this.humidityMeasureType = humidityMeasureType;
    }

    public int getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getStressStartTime() {
        return stressStartTime;
    }

    public void setStressStartTime(int stressStartTime) {
        this.stressStartTime = stressStartTime;
    }

    public int getStressEndTime() {
        return stressEndTime;
    }

    public void setStressEndTime(int stressEndTime) {
        this.stressEndTime = stressEndTime;
    }

    public int getStressTimeType() {
        return stressTimeType;
    }

    public void setStressTimeType(int stressTimeType) {
        this.stressTimeType = stressTimeType;
    }

    public int getStressResultType() {
        return stressResultType;
    }

    public void setStressResultType(int stressResultType) {
        this.stressResultType = stressResultType;
    }

    public long getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(long averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public long getMinHeartRate() {
        return minHeartRate;
    }

    public void setMinHeartRate(long minHeartRate) {
        this.minHeartRate = minHeartRate;
    }

    public long getMaxHeartRate() {
        return MaxHeartRate;
    }

    public void setMaxHeartRate(long maxHeartRate) {
        MaxHeartRate = maxHeartRate;
    }

    public int getHeartHealth() {
        return heartHealth;
    }

    public void setHeartHealth(int heartHealth) {
        this.heartHealth = heartHealth;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    public String getUnicodeContent() {
        return TextUtils.isEmpty(unicodeContent) ? "0" : unicodeContent;
    }

    public void setUnicodeContent(String unicodeContent) {
        this.unicodeContent = unicodeContent;
    }

    String unicodeContent;

    public String getUnicodeTitle() {
        return TextUtils.isEmpty(unicodeTitle) ? "0" : unicodeTitle;
    }

    public void setUnicodeTitle(String unicodeTitle) {
        this.unicodeTitle = unicodeTitle;
    }

    public String getCharacteristic() {
        return TextUtils.isEmpty(characteristic) ? "0" : characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public byte getUnicodeType() {
        return unicodeType;
    }

    public void setUnicodeType(byte unicodeType) {
        this.unicodeType = unicodeType;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getAirQuality() {
        return TextUtils.isEmpty(airQuality) ? "0" : airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getUVIndex() {
        return TextUtils.isEmpty(UVIndex) ? "0" : UVIndex;
    }

    public void setUVIndex(String UVIndex) {
        this.UVIndex = UVIndex;
    }

    public int getSunriseHours() {
        return sunriseHours;
    }

    public void setSunriseHours(int sunriseHours) {
        this.sunriseHours = sunriseHours;
    }

    public int getSunriseMin() {
        return sunriseMin;
    }

    public void setSunriseMin(int sunriseMin) {
        this.sunriseMin = sunriseMin;
    }

    public int getSunsetHours() {
        return sunsetHours;
    }

    public void setSunsetHours(int sunsetHours) {
        this.sunsetHours = sunsetHours;
    }

    public int getSunsetMin() {
        return sunsetMin;
    }

    public void setSunsetMin(int sunsetMin) {
        this.sunsetMin = sunsetMin;
    }

    public String getHighestTemperatureToday() {
//        return highestTemperatureToday;
        return TextUtils.isEmpty(highestTemperatureToday) ? "0" : highestTemperatureToday;
    }

    public void setHighestTemperatureToday(String highestTemperatureToday) {
        this.highestTemperatureToday = highestTemperatureToday;
    }

    public String getLowestTemperatureToday() {
        return TextUtils.isEmpty(lowestTemperatureToday) ? "0" : lowestTemperatureToday;

    }

    public void setLowestTemperatureToday(String lowestTemperatureToday) {
        this.lowestTemperatureToday = lowestTemperatureToday;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public void setSystolicBloodPressure(int systolicBloodPressure) {
        this.systolicBloodPressure = systolicBloodPressure;
    }

    public int getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public void setDiastolicBloodPressure(int diastolicBloodPressure) {
        this.diastolicBloodPressure = diastolicBloodPressure;
    }

    public String getHumidity() {
        return TextUtils.isEmpty(humidity) ? "0" : humidity;

    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }


    public long getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(long totalSteps) {
        this.totalSteps = totalSteps;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getCalories() {
        return Calories;
    }

    public void setCalories(long calories) {
        Calories = calories;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public int getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(int bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getTemperature() {
        return TextUtils.isEmpty(temperature) ? "0" : temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getPedometer() {
        return pedometer;
    }

    public void setPedometer(int pedometer) {
        this.pedometer = pedometer;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }


}
