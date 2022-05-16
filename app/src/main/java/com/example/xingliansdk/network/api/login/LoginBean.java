package com.example.xingliansdk.network.api.login;

import java.util.Locale;

public class LoginBean {
    /**
     * token : 33579664123466
     * user : {"user_id":"","nickname":"系统管理员","height":"","weight":200,"age":"","sex":"","birthDate":"","phone":"18218535963","headPortrait":""}
     * userConfig : {"unitSetting":"","doNotDisturb":"","schedule":"","movingTarget":"","sleepTarget":"","callReminder":"","smsReminder":"","orderReminder":"","alarmClock":"","sedentary":"","drinkWater":"","takeMedicine":"","heartRateAlarm":"","turnScreen":""}
     */
    private String token;
    private UserDTO user;
    private UserConfigDTO userConfig;
    private permissionDTO permission;
    private String msg;
    private String verifyCode;
    private Boolean register;

    public permissionDTO getPermission() {
        return permission;
    }

    public void setPermission(permissionDTO permission) {
        this.permission = permission;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserConfigDTO getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(UserConfigDTO userConfig) {
        this.userConfig = userConfig;
    }

    public static class UserDTO {
        /**
         * user_id :
         * nickname : 系统管理员
         * height :
         * weight : 200
         * age :
         * sex :
         * birthDate :
         * phone : 18218535963
         * headPortrait :
         */
        private int id;
        private String userId;
        private String nickname;
        private String height;
        private String weight;
        private String age;
        private String sex;
        private String birthDate;
        private String phone;
        private String headPortrait;
        private String createTime;
        private String updateTime;
        private String name;
        private String areaCode;
        private String mac;

        public String getMac() {
            return mac != null ? mac.toUpperCase(Locale.ROOT) : mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }
    }

    public static class UserConfigDTO {
        /**
         * unitSetting :
         * doNotDisturb :
         * schedule :
         * movingTarget :
         * sleepTarget :
         * callReminder :
         * smsReminder :
         * orderReminder :
         * alarmClock :
         * sedentary :
         * drinkWater :
         * takeMedicine :
         * heartRateAlarm :
         * turnScreen :
         */

//        private String unitSetting;
        private String doNotDisturb;
        private String schedule;
        private String movingTarget;
        private String sleepTarget;
        private String callReminder;
        private String smsReminder;
        private String orderReminder;
        private String alarmClock;
        private String sedentary;
        private String drinkWater;
        private String takeMedicine;
        private String heartRateAlarm;
        private String turnScreen;
        private int distanceUnit; //距离单位
        private int temperatureUnit;//温度单位
        private int timeFormat;// 时间制式
        private int heartRateThreshold;//心率报警临界值

        //夜间睡眠血压测量 1关2开
        private int bloodPressureNightSleepMeasurement;
        //非睡眠状态血压测量 1关 2开
        private int bloodPressureNonSleepMeasurement;

        public int getDistanceUnit() {
            return distanceUnit;
        }

        public void setDistanceUnit(int distanceUnit) {
            this.distanceUnit = distanceUnit;
        }

        public int getTemperatureUnit() {
            return temperatureUnit;
        }

        public void setTemperatureUnit(int temperatureUnit) {
            this.temperatureUnit = temperatureUnit;
        }

        public int getTimeFormat() {
            return timeFormat;
        }

        public void setTimeFormat(int timeFormat) {
            this.timeFormat = timeFormat;
        }

        public int getHeartRateThreshold() {
            return heartRateThreshold;
        }

        public void setHeartRateThreshold(int heartRateThreshold) {
            this.heartRateThreshold = heartRateThreshold;
        }

        public String getDoNotDisturb() {
            return doNotDisturb;
        }

        public void setDoNotDisturb(String doNotDisturb) {
            this.doNotDisturb = doNotDisturb;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public String getMovingTarget() {
            return movingTarget;
        }

        public void setMovingTarget(String movingTarget) {
            this.movingTarget = movingTarget;
        }

        public String getSleepTarget() {
            return sleepTarget;
        }

        public void setSleepTarget(String sleepTarget) {
            this.sleepTarget = sleepTarget;
        }

        public String getCallReminder() {
            return callReminder;
        }

        public void setCallReminder(String callReminder) {
            this.callReminder = callReminder;
        }

        public String getSmsReminder() {
            return smsReminder;
        }

        public void setSmsReminder(String smsReminder) {
            this.smsReminder = smsReminder;
        }

        public String getOrderReminder() {
            return orderReminder;
        }

        public void setOrderReminder(String orderReminder) {
            this.orderReminder = orderReminder;
        }

        public String getAlarmClock() {
            return alarmClock;
        }

        public void setAlarmClock(String alarmClock) {
            this.alarmClock = alarmClock;
        }

        public String getSedentary() {
            return sedentary;
        }

        public void setSedentary(String sedentary) {
            this.sedentary = sedentary;
        }

        public String getDrinkWater() {
            return drinkWater;
        }

        public void setDrinkWater(String drinkWater) {
            this.drinkWater = drinkWater;
        }

        public String getTakeMedicine() {
            return takeMedicine;
        }

        public void setTakeMedicine(String takeMedicine) {
            this.takeMedicine = takeMedicine;
        }

        public String getHeartRateAlarm() {
            return heartRateAlarm;
        }

        public void setHeartRateAlarm(String heartRateAlarm) {
            this.heartRateAlarm = heartRateAlarm;
        }

        public String getTurnScreen() {
            return turnScreen;
        }

        public void setTurnScreen(String turnScreen) {
            this.turnScreen = turnScreen;
        }

        public int getBloodPressureNightSleepMeasurement() {
            return bloodPressureNightSleepMeasurement;
        }

        public void setBloodPressureNightSleepMeasurement(int bloodPressureNightSleepMeasurement) {
            this.bloodPressureNightSleepMeasurement = bloodPressureNightSleepMeasurement;
        }

        public int getBloodPressureNonSleepMeasurement() {
            return bloodPressureNonSleepMeasurement;
        }

        public void setBloodPressureNonSleepMeasurement(int bloodPressureNonSleepMeasurement) {
            this.bloodPressureNonSleepMeasurement = bloodPressureNonSleepMeasurement;
        }
    }

    public static class permissionDTO {
        String flashUpdate;

        public String getFlashUpdate() {
            return flashUpdate;
        }

        public void setFlashUpdate(String flashUpdate) {
            this.flashUpdate = flashUpdate;
        }
    }


}
