package com.shon.connector.bean;

public class DeviceInformationBean {
    int sex;
    int age;
    int height;
    /***
     * 体重
     * */
    float weight;
    /**
     * 系统语言
     */
    int language;
    /***时间制式*/
    int timeSystem;
    /***
     * 系统选择
     */
    int android=01; //0x01安卓
    /**
     * 单位制式
     */
    int unitSystem;
    /**
     * 左右手佩戴
     */
    int wearHands;
    /**
     * 温度制式
     */
    int temperatureSystem;
    /**
     * 运动达标步数
     */
    long exerciseSteps;
    /**
     * 出生日期 ,
     */
    long birth;
    /**
     * 昵称
     */
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBirth() {
        return birth;
    }

    public void setBirth(long birth) {
        this.birth = birth;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int  getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getWeight() {
        return  weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public byte getLanguage() {
        return (byte)language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public byte getTimeSystem() {
        return (byte)timeSystem;
    }

    public void setTimeSystem(int timeSystem) {
        this.timeSystem = timeSystem;
    }

    public byte getAndroid() {
        return (byte)android;
    }

    public void setAndroid(int android) {
        this.android = android;
    }

    public byte getUnitSystem() {
        return (byte)unitSystem;
    }

    public void setUnitSystem(int unitSystem) {
        this.unitSystem = unitSystem;
    }

    public byte getWearHands() {
        return (byte)wearHands;
    }

    public void setWearHands(int wearHands) {
        this.wearHands = wearHands;
    }

    public byte getTemperatureSystem() {
        return (byte) temperatureSystem;
    }

    public void setTemperatureSystem(int temperatureSystem) {
        this.temperatureSystem = temperatureSystem;
    }

    public long getExerciseSteps() {
        return exerciseSteps;
    }

    public void setExerciseSteps(long exerciseSteps) {
        this.exerciseSteps = exerciseSteps;
    }
    public DeviceInformationBean(){}
    public DeviceInformationBean(int sex, int age, int height, float weight, int language, int timeSystem, int android, int unitSystem, int wearHands, int temperatureSystem, long exerciseSteps) {
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.language = language;
        this.timeSystem = timeSystem;
        this.android = android;
        this.unitSystem = unitSystem;
        this.wearHands = wearHands;
        this.temperatureSystem = temperatureSystem;
        this.exerciseSteps = exerciseSteps;
    }
    public DeviceInformationBean(int sex, int age, int height, float weight, int language, int timeSystem, int android, int unitSystem, int wearHands, int temperatureSystem, long exerciseSteps,long birth) {
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.language = language;
        this.timeSystem = timeSystem;
        this.android = android;
        this.unitSystem = unitSystem;
        this.wearHands = wearHands;
        this.temperatureSystem = temperatureSystem;
        this.exerciseSteps = exerciseSteps;
        this.birth=birth;
    }

    public DeviceInformationBean(int sex, int age, int height, float weight, int language, int timeSystem, int android, int unitSystem, int wearHands, int temperatureSystem, long exerciseSteps, long birth, String name) {
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.language = language;
        this.timeSystem = timeSystem;
        this.android = android;
        this.unitSystem = unitSystem;
        this.wearHands = wearHands;
        this.temperatureSystem = temperatureSystem;
        this.exerciseSteps = exerciseSteps;
        this.birth = birth;
        this.name = name;
    }


    @Override
    public String toString() {
        return "DeviceInformationBean{" +
                "sex=" + sex +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", language=" + language +
                ", timeSystem=" + timeSystem +
                ", android=" + android +
                ", unitSystem=" + unitSystem +
                ", wearHands=" + wearHands +
                ", temperatureSystem=" + temperatureSystem +
                ", exerciseSteps=" + exerciseSteps +
                ", birth=" + birth +
                ", name='" + name + '\'' +
                '}';
    }
}
