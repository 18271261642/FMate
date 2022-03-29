package com.example.xingliansdk;

public class Config {
    /**
     * 数据库的 类名
     */
    public class database {
        //个人信息
        public static final String USER_INFO = "userInfo";
        //绑定手机uuid
        public static final String BIND_UUID = "uuid";
        //ota 模式
        public static final String DEVICE_OTA = "ota";
        //个人
        //头像保存
        public static final String IMG_HEAD = "imgHead";
        //首页数据存储
        public static final String HOME_CARD_BEAN = "HomeCardBean";

        //寻找设备功能开关
        public static final String FIND_DEVICE_SWITCH = "FindDeviceSwitch";
        //APP端开启设备勿扰模式开关
        public static final String DO_NOT_DISTURB_MODE_SWITCH = "DoNotDisturbModeSwitch";
        //APP端开启设备抬手亮屏开关
//        public static final String TURN_ON_SCREEN = "TurnOnScreen";
        //APP端开启设备喝水提醒开关
        public static final String REMINDER_DRINK_WATER = "ReminderDrinkWater";
        //APP端开启设备久坐提醒开关
        public static final String REMINDER_SEDENTARY = "ReminderSedentary";
        //心率报警开关
        public static final String HEART_RATE_ALARM = "HeartRateAlarm";
        //来电开关
        public static final String INCOMING_CALL = "IncomingCall";
        //短信开关
        public static final String SMS = "SMS";
        //其他通知总开关other
        public static final String OTHER = "OTHER";
        ////////////////////////////设置////////////////////////
        //3.6.1 APP端设置设备时间
        public static final String SET_TIME = "time";
        //3.6.2 APP端设置设备基本信息
        public static final String PERSONAL_INFORMATION = "personalInformation";
        //睡眠目标
        public static final String SLEEP_GOAL = "sleepGoal";
        //3.6.6 APP端设置设备闹钟，日程
        public static final String TIME_LIST = "TimeList";
        public static final String SCHEDULE_LIST = "ScheduleList";
        //3.6.7 APP端设置设备天气参数
        public static final String WEATHER = "Weather";
        //3.6.8 APP端设置设备吃药提醒
        public static final String REMIND_TAKE_MEDICINE = "RemindTakeMedicine";
        //获取设备属性信息
        public static final String DEVICE_ATTRIBUTE_INFORMATION = "DeviceAttributeInformation";
        //获取发送的消息存储库
        public static final String MESSAGE_CALL="MessageCall";



        public static final String SENSOR_STEP_ACTION = "com.example.xingliansdk.sensor";




        //用于保存运动类型，步行、跑步等
        public static final String AMAP_SPORT_TYPE = "map_sport_type";

        //保存步行的总距离
        public static final String WALK_DISTANCE_KEY = "walk_distance_type";
        //保存跑步总距离
        public static final String RUN_DISTANCE_KEY = "run_distance_type";
        //保存骑行的总距离
        public static final String BIKE_DISTANCE_KEY = "bike_distance_type";
        //闹钟修改最新时间
        public static final String ALARM_CLOCK_CREATE_TIME = "AlarmClockCreateTime";
        //日程修改最新时间
        public static final String SCHEDULE_CREATE_TIME = "ScheduleCreateTime";
        //吃药修改最新时间
        public static final String TAKE_MEDICINE_CREATE_TIME = "takeMedicineCreateTime";

    }

    public class eventBus {

        public static final int LOCATION_INFO = 2020;
        public static final int DEFAULT = 1000;
        /**
         * 设备固件信息
         */
        public static final int DEVICE_FIRMWARE = DEFAULT + 1;
        /**
         * 链接并返回首页
         */
        public static final int DEVICE_CONNECT_HOME = DEFAULT + 101;

        /**
         * 链接上并打开监听与通知
         */
        public static final int DEVICE_CONNECT_NOTIFY = DEFAULT + 100;
        /**
         * 电量监听
         */
        public static final int DEVICE_ELECTRICITY = DEFAULT + 102;

        /**
         * 首页展示数据
         */
        public static final int HOME_CARD = DEFAULT + 103;

        /**
         * 断开链接回调
         */
        public static final int DEVICE_DISCONNECT = DEFAULT + 104;
        /**
         * 关闭蓝牙
         */
        public static final int DEVICE_BLE_OFF = DEFAULT + 113;
        /**
         * 删除设备
         */
        public static final int DEVICE_DELETE_DEVICE = DEFAULT + 105;
        /**
         * OTA升级业务
         */
        public static final int DEVICE_OTA_UPDATE = DEFAULT + 106;
        /**
         * OTA升级完成
         */
        public static final int DEVICE_OTA_UPDATE_COMPLETE  = DEFAULT + 107;

        /**
         * 首次链接,无法链接成功返回outTime
         */
        public static final int DEVICE_TIME_OUT = DEFAULT + 108;
        /**
         * 删除设备 首页
         */
        public static final int DEVICE_DELETE_DEVICE_HOME = DEFAULT + 109;
        /**
         * 连接上了并发送天气信息给硬件
         */
        public static final int DEVICE_CONNECT_WEATHER_SERVICE = DEFAULT + 110;
        /**
         * 遥控拍照
         */
        public static final int DEVICE_REMOTE_CAMERA = DEFAULT + 111;
        /**
         * 充电状态 00非充电,01充电,02充电且满了
         */
        public static final int DEVICE_ELECTRICITY_STATUS = DEFAULT + 112;
        /**
         * 吃药提醒周期
         */
        public static final int REMIND_TAKE_MEDICINE_REMINDER_PERIOD = DEFAULT + 200;
        /**
         * 勿扰模式时间回调开启时间与关闭时间
         */
        public static final int DO_NOT_DISTURB_TIME_OPEN = DEFAULT + 201;
        public static final int DO_NOT_DISTURB_TIME_CLOSE = DEFAULT + 202;

        /**
         * 首次链接,无法链接成功返回outTime
         */
        public static final int DEVICE_NOTIFY_TIME_OUT = DEFAULT + 203;
        /**
         * 更新运动达标计步
         */
        public static final int SPORTS_GOAL_EXERCISE_STEPS = DEFAULT + 300;
        /**
         * 更新睡眠目标
         */
        public static final int SPORTS_GOAL_SLEEP = DEFAULT + 301;
        /**
         * 地图运动回调不满足条件不保存
         */
        public static final int MAP_MOVEMENT_DISSATISFY = DEFAULT + 400;
        /**
         * 地图运动回调保存记录成功
         */
        public static final int MAP_MOVEMENT_SATISFY = DEFAULT + 401;
        /**
         * 计步器回调
         */
        public static final int MAP_MOVEMENT_STEP = DEFAULT + 402;
        /**
         * 血压记录回调
         */
        public static final int BLOOD_PRESSURE_RECORD = DEFAULT + 500;
        /**
         * 首次链接成功时 刷新所有大数据当天操作
         */
        public static final int HOME_HISTORICAL_BIG_DATA = DEFAULT + 600;
        /**
         * 当天大数据拿完以后是否需要拿去 前面几天的大数据
         */
        public static final int HOME_HISTORICAL_BIG_DATA_WEEK = DEFAULT + 601;
        /**
         * 刷新表盘数据
         */
        public static final  int DIAL_RECOMMEND_DIAL=DEFAULT +700;
        /**
         * 刷新自定义表盘数据
         */
        public static final  int DIAL_CUSTOMIZE=DEFAULT +701;
        /**
         * 表盘进度条
         */
        public static final  int DIAL_IMG_RECOMMEND_DETAIL =DEFAULT +702;
        /**
         * 推荐_表盘进度条
         */
        public static final  int DIAL_IMG_RECOMMEND_INDEX=DEFAULT +703;
        /**
         * 头像更换保存
         */
        public static final int EVENT_BUS_IMG_HEAD = DEFAULT + 1001;
        /**
         * FLASH 更新
         */
        public static final int FLASH_UPDATE = DEFAULT + 1002;
        /**
         * 更换单位
         */
        public static final int CHANGE_UNIT = DEFAULT + 1003;
        /**
         * 主动上传设备当前id
         */
        public static final int DEVICE_DIAL_ID = DEFAULT + 1004;
    }

    public class exercise {
        /**
         * 健走
         */
        public static final double WALK = 0.8214;
        /**
         * 跑步
         */
        public static final double RUN = 1.036;
        /**
         * 自行车
         */
        public static final double BICYCLE = 0.6142;
        /**
         * 爬山
         */
        public static final double MOUNTAIN_CLIMBING = 0.888;//其实这个是室外滑冰的
        /**
         * 英里单位转换
         */
        public static final double MILE = 0.6213;
    }
}
