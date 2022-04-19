package com.shon.connector;

public class Config {

    /**
     * 温度最大值 当大于这个值时即温度为-温度
     */
    public static final int TEMPERATURE_MAX = 32768;
    /**
     * 硬件方是 2000年起步 和我们的不同所以要加一个时间
     */
    public static final long TIME_START = 946656000L;


    //保存当前手表显示的表盘ID
    public static final String SAVE_DEVICE_CURRENT_DIAL = "device_current_dial";
    //保存手表中存在表盘市场的表的表id
    public static final String SAVE_DEVICE_INTO_MARKET_DIAL = "device_market_dial";
    //保存一个市场表盘的序列化对象，用于在本地表盘中
    public static final String SAVE_MARKET_BEAN_DIAL = "device_market_bean";
    //自定义表盘的图片URL
    public static final String SAVE_LOCAL_CUS_DIAL_URL = "cus_dial_url";

    /**
     * 湿度最大值 当大于这个值时即温度为-温度
     */
    public static final int HUMIDITY_MAX = 1000;
    /**
     * 心率,舒张压,收缩压最大值
     */
    public static final int BLE_MAX = 250;
    /**
     * 心率,舒张压,收缩压最小值
     */
    public static final int BLE_FORTY = 40;
    /**
     * 血氧大值
     */
    public static final int BLOOD_OXYGEN_MAX = 100;
    /**
     * 默认值
     */
    public static final int DEFAULT = 0;
    /**
     *控制指令关
     */
    public static final int OFF = 1;
    /**
     *控制指令开
     */
    public static final int ON = 2;
    /**
     * 产品代号
     */
    public static final byte PRODUCT_CODE = (byte) 0x88;
    /**
     * 取低五位
     */
    public static final byte LOWER_FIVE = (byte) 0x1F;
    /**
     * 取低12位
     */
    public static final byte LOWER_12 = (byte) 0x1FFF;
    /**
     * 设备端返回ack指令
     */
    public static final byte DEVICE_COMMAND_ACK = 0x07;
    /**
     * 设备端返回ack指令
     */
    public static final byte DEVICE_KEY_ACK = 0x02;
    /**
     * 指定头长度
     */
    public static final int BYTE_HEAD = 8;
    /**
     * 解绑蓝牙
     */
    public static final int UNBIND_BLE = 10001;

    /**
     * SDK正式板子服务特征
     */
    public static final String serviceUUID = "1F40EAF8-AAB4-14A3-F1BA-F61F35CDDBAA";
    public static final String mWriteCharacter = "1F400001-AAB4-14A3-F1BA-F61F35CDDBAA";
    public static final String readCharacter = "1F400002-AAB4-14A3-F1BA-F61F35CDDBAA";
    public static final String WriteCharacterBig = "1F400003-AAB4-14A3-F1BA-F61F35CDDBAA";
    public static final String readCharacterBig = "1F400004-AAB4-14A3-F1BA-F61F35CDDBAA";
    public static final String OTAServiceUUID = "8EC9FE59-F315-4F60-9FB8-838830DAEA50";
    public class getDevice {

        public static final byte COMMAND = 0x00;
        public static final byte DEVICE_ACK = 0x01;
        /**
         * 设备端同步设备固件信息
         */
        public static final byte DEVICE_FIRMWARE_KEY = 0x02;
        /**
         * 设备端同步设备属性信息
         */
        public static final byte DEVICE_PROPERTIES_KEY = 0x04;
        /**
         * 设备端同步模块测量状态
         */
        public static final byte DEVICE_MODULE_MEASUREMENT_KEY = 0x06;
        /**
         * 设备端同步实时运动数据
         */
        public static final byte DEVICE_REAL_TIME_EXERCISE_KEY = 0x08;
        /**
         * APP 端同步实时运动数据
         */
        public static final byte APP_REAL_TIME_EXERCISE_KEY = 0x07;
        /**
         * 设备端同步血压校准值
         */
        public static final byte DEVICE_BLOOD_PRESSURE_CALIBRATION_KEY = 0x0C;
        /**
         * 设备端同步大数据存储间隔
         */
        public static final byte DEVICE_BIG_DATA_INTERVAL_KEY = 0x0E;
        /**
         * 设备端同步大数据存储间隔
         */
        public static final byte DEVICE_UUID_BIND_KEY = 0x10;
        /**
         * 设备端同步设备 FLASH 特征编号
         */
        public static final byte DEVICE_FLASH_FEATURES_KEY = 0x12;
    }

    public class ControlClass {

        public static final byte COMMAND = 0x01;
        public static final byte DEVICE_ACK = 0x01;
        /**
         * 设备端同步设备固件信息
         */
        public static final byte DEVICE_FIRMWARE_KEY = 0x02;
        /**
         * APP 端开启设备实时上传心率值开关
         */
        public static final byte APP_REAL_TIME_HEART_RATE_SWITCH_KEY = 0x04;
        /**
         * APP 端开启设备实时上传血氧值开关
         */
        public static final byte APP_REAL_TIME_BLOOD_OXYGEN_SWITCH_KEY = 0x05;
        /**
         * APP 端开启设备实时上传血压值开关
         */
        public static final byte APP_REAL_TIME_BLOOD_PRESSURE_SWITCH_KEY = 0x06;
        /**
         * APP 端开启设备实时上传温度开关
         */
        public static final byte APP_REAL_TIME_TEMPERATURE_SWITCH_KEY = 0x07;
        /**
         * APP 端开启寻找设备功能开关
         */
        public static final byte APP_FIND_DEVICE_KEY = 0x08;
        /**
         * APP 端开启设备勿扰模式开关
         */
        public static final byte APP_DO_NOT_DISTURB_SWITCH_KEY = 0x09;
        /**
         * APP 端开启设备相机开关
         */
        public static final byte APP_DEVICE_CAMERA_SWITCH_KEY = 0x0A;
        /**
         *  APP 端开启设备抬手亮屏开关
         */
        public static final byte APP_HAND_BRIGHT_SCREEN_KEY = 0x0C;
        /**
         * APP 端开启设备喝水提醒开关
         */
        public static final byte APP_DRINK_WATER_REMINDER_SWITCH_KEY = 0x0E;
        /**
         *  APP 端开启设备久坐提醒开关
         */
        public static final byte APP_SEDENTARY_REMINDER_KEY = 0x0F;



        /**
         * APP 端开启设备自动测量心率开关
         */
        public static final byte APP_MEASURING_HEART_RATE_SWITCH = 0x10;
        /**
         * APP 端开启设备自动测量血氧开关
         */
        public static final byte APP_MEASURING_BLOOD_OXYGEN_SWITCH = 0x11;
        /**
         * APP 端开启设备实时上传血压值开关
         */
        public static final byte APP_BLOOD_PRESSURE_SWITCH = 0x12;
        /**
         * APP 端开启设备自动测量温度开关
         */
        public static final byte APP_TEMPERATURE_SWITCH = 0x13;
        /**
         * APP 端开启设备自动测量湿度开关
         */
        public static final byte APP_HUMIDITY_SWITCH = 0x15;

        /**
         * 设备端同步设备属性信息
         */
        public static final byte DEVICE_MODULE_MEASUREMENT_KEY = 0x06;
    }

    public class BigData {
        /**
         * 大数据指定Key
         */
        public static final byte KEY = 0X02;
        public static final byte DEVICE_ACK = 0x01;
        /**************************************************************************记录操作***********************************************/
        /**
         * APP 端获取心率大数据记录
         */
        public static final byte APP_HEART_RATE = 0x01;
        /**
         * APP 端获取血氧大数据记录
         */
        public static final byte APP_BLOOD_OXYGEN = 0x09;
        /**
         * APP 端获取呼吸暂停现象大数据记录
         */
        public static final byte APP_APNEA = 0x0D;
        /**
         * APP 端获取血压大数据记录
         */
        public static final byte APP_BLOOD_PRESSURE = 0x11;
        /**
         * APP 端获取温度大数据记录
         */
        public static final byte APP_TEMPERATURE = 0x15;
        /**
         * APP 端获取RR大数据记录
         */
        public static final byte APP_RR = 0x19;
        /**
         * APP 端获取压力度疲劳度大大数据记录
         */
        public static final byte APP_STRESS_FATIGUE = 0x1D;
        /**
         * APP端获取日常活动大数据记录
         */
        public static final byte APP_DAILY_ACTIVITIES = 0x21;
        /**
         * APP端获取睡眠大数据记录
         */
        public static final byte APP_SLEEP = 0x25;
        /**
         * APP 端获取运动大数据记录
         */
        public static final byte APP_SPORTS = 0x29;
        /**
         * APP 端获取运动状态识别大数据记录
         */
        public static final byte APP_SPORTS_TYPE = 0x2D;
        /**************************************************************************设备操作***********************************************/
        /**
         * 设备端同步心率大数据记录
         */
        public static final byte DEVICE_HEART_RATE = 0x02;
        /**
         * 设备端同步血氧大数据记录
         */
        public static final byte DEVICE_BLOOD_OXYGEN = 0x0A;
        /**
         * 设备端同步呼吸暂停现象大数据记录
         */
        public static final byte DEVICE_APNEA = 0x0E;
        /**
         * 设备端同步血压大数据记录
         */
        public static final byte DEVICE_BLOOD_PRESSURE = 0x12;
        /**
         * 设备端同步温度大数据记录
         */
        public static final byte DEVICE_TEMPERATURE = 0x16;
        /**
         * 设备端同步RR大数据记录
         */
        public static final byte DEVICE_RR = 0x1A;
        /**
         * 设备端同步压力度疲劳度大大数据记录
         */
        public static final byte DEVICE_STRESS_FATIGUE = 0x1E;
        /**
         * 设备端同步日常活动大数据记录
         */
        public static final byte DEVICE_DAILY_ACTIVITIES = 0x22;
        /**
         * 设备端同步睡眠大数据记录
         */
        public static final byte DEVICE_SLEEP = 0x26;
        /**
         * 设备端同步运动大数据记录
         */
        public static final byte DEVICE_SPORTS = 0x2A;
        /**
         * 设备端同步运动状态识别记录
         */
        public static final byte DEVICE_SPORTS_TYPE = 0x2E;

/**************************************************************************指定操作***********************************************/

        /**
         * APP 端获取指定的心率数据
         */
        public static final String APP_SPECIFY_HEART_RATE = "03";
        /**
         * APP 端获取指定的血氧数据
         */
        public static final String APP_SPECIFY_BLOOD_OXYGEN = "0B";
        /**
         * APP 端获取指定的呼吸暂停现象数据
         */
        public static final String APP_SPECIFY_APNEA = "0F";
        /**
         * APP 端获取指定的血压数据
         */
        public static final String APP_SPECIFY_BLOOD_PRESSURE = "13";
        /**
         * APP 端获取指定的温度数据
         */
        public static final String APP_SPECIFY_TEMPERATURE = "17";
        /**
         * APP 端获取指定的RR数据
         */
        public static final String APP_SPECIFY_RR = "1B";
        /**
         * APP 端获取指定的压力度疲劳度数据
         */
        public static final String APP_SPECIFY_STRESS_FATIGUE = "1F";

        /**
         * APP端获取指定日常活动大数据记录
         */
        public static final String APP_SPECIFY_DAILY_ACTIVITIES = "23";
        /**
         * APP端获取指定睡眠大数据记录
         */
        public static final String APP_SPECIFY_SLEEP = "27";
        /**
         * APP 端获取指定的运动数据
         */
        public static final String APP_SPECIFY_SPORTS = "2B";
        /**
         * APP 端获取指定的运动状态识别数据
         */
        public static final String APP_SPECIFY_SPORTS_TYPE = "2F";
        /**
         * 设备端同步指定的心率数据
         */
        public static final byte DEVICE_SPECIFY_HEART_RATE = 0x04;
        /**
         * 设备端同步指定的血氧数据
         */
        public static final byte DEVICE_SPECIFY_BLOOD_OXYGEN = 0x0C;
        /**
         * 设备端同步指定的呼吸暂停现象数据
         */
        public static final byte DEVICE_SPECIFY_APNEA = 0x10;
        /**
         * 设备端同步指定的呼吸暂停现象数据
         */
        public static final byte DEVICE_SPECIFY_BLOOD_PRESSURE = 0x14;
        /**
         * 设备端同步指定的温度数据
         */
        public static final byte DEVICE_SPECIFY_TEMPERATURE = 0x18;
        /**
         * 设备端同步指定的RR数据
         */
        public static final byte DEVICE_SPECIFY_RR = 0x1C;
        /**
         * 设备端同步指定的压力度疲劳度数据
         */
        public static final byte DEVICE_SPECIFY_STRESS_FATIGUE = 0x20;
        /**
         * 设备端同步指定的日常活动数据
         */
        public static final byte DEVICE_SPECIFY_DAILY_ACTIVITIES = 0x24;
        /**
         * 设备端同步指定的睡眠数据
         */
        public static final byte DEVICE_SPECIFY_SLEEP = 0x28;
        /**
         * 设备端同步指定的运动数据
         */
        public static final byte DEVICE_SPECIFY_SPORTS = 0x2C;
        /**
         * 设备端同步指定的运动状态识别数据
         */
        public static final byte DEVICE_SPECIFY_SPORTS_TYPE = 0x30;
        /**
         * 测试
         */
        public static final int TEST = 60;


    }

    public class ActiveUpload{
        public static final byte COMMAND = 0x03;
        /**
         *设备端主动上传实时运动数据
         */
        public static final byte DEVICE_REAL_TIME_EXERCISE = 0x01;
        /**
         *设备端主动上传其他实时数据
         */
        public static final byte DEVICE_REAL_TIME_OTHER = 0x02;
        /**
         *设备端主动发起找手机功能
         */
        public static final byte DEVICE_FIND_PHONE = 0x03;
        /**
         *设备端主动发起改变手机来电状态
         */
        public static final byte DEVICE_CHANGE_PHONE_CALL_STATUS = 0x04;
        /**
         *设备端主动发起相机确认信号
         */
        public static final byte DEVICE_CAMERA_CONFIRMATION = 0x05;
        /**
         *设备端主动发起音乐控制信号
         */
        public static final byte DEVICE_MUSIC_CONTROL_KEY = 0x06;
        /**
         *设备端主动发起告警信号
         */
        public static final byte DEVICE_WARNING_SIGNAL_KEY = 0x07;
        /**
         * 设备端主动上传设备电量变化信息
         */
        public static final byte DEVICE_POWER_CHANGE_KEY = 0x08;

        /**
         * 设备端主动上传设备端当前表盘 ID
         */
        public static final byte DEVICE_DIAL_ID = 0x0A;

    }

    public class SettingDevice {
        public static final String command = "04";
        /**
         * APP 端设置设备时间
         */
        public static final String APP_TIME = "01";
        /**
         * APP 端设置设备基本信息
         */
        public static final String APP_BASIC_INFORMATION = "02";
        /**
         * APP 端设置设备实时运动数据的上传方式
         */
        public static final String APP_MOVEMENT_TYPE = "03";
        //默认
        public static final int APP_MOVEMENT_TYPE_DEVICE = 0;
        //实时上传运动数据
        public static final int APP_MOVEMENT_TYPE_REAL_TIME_UPLOAD = 1;
        /**
         * APP 端设置设备血压校准值
         */
        public static final String APP_BLOOD_PRESSURE = "04";

        /**
         * APP 端设置设备大数据存储间隔
         */
        public static final String APP_BIG_DATA_STORAGE_INTERVAL = "05";
        /**
         * APP 端设置设备闹钟，日程
         */
        public static final String APP_ALARM_CLOCK = "06";
        /**
         * APP 端设置设备天气参数
         */
        public static final String APP_WEATHER = "07";
        /**
         * APP 设备吃药提醒
         */
        public static final String APP_REMIND_TAKE_MEDICINE = "08";
    }

    public class Expand {
        /**
         * app端返回ack指令
         */
        public static final byte APP_ACK = 0x01;
        /**
         * 设备端返回ack指令
         */
        public static final byte DEVICE_ACK = 0x02;
        /**
         * 设备端返回ack指令
         */
        public static final byte COMMAND = 0x07;
    }
    public class Flash{
        public static final byte KEY = 0X08;
        /**
         * 设备端同步已擦写 FLASH 数据块的状态
         */
        public static final byte DEVICE_ERASURE = 0X04;
        /**
         * 设备端同步完成更新 FLASH 数据块的状态
         */
        public static final byte DEVICE_FLASH_OK_UPDATE = 0X06;
    }

    public class Dial{
        public static final byte KEY = 0X09;

        /**
         *
         */
        public static final byte DEVICE_DIAL_FLASH = 0X02;
        /**
         3.11.8 设备端同步 APP 端设定的表盘的状态（已传输到设备端的情形）
         */
        public static final byte DEVICE_DIAL_DESIGNATED = 0X08;
        /**
         * 3.11.4 设备端同步指定非固化表盘概要信息状态
         */
        public static final byte DEVICE_DIAL_STATUS = 0X04;
        /**
         * 设备端同步完成更新 FLASH 数据块的状态
         */
        public static final byte DEVICE_FLASH_OK_UPDATE = 0X06;
    }
}
