package com.example.xingliansdk.eventbus;

/**
 * 功能:App各种事件
 */

public class AppEvent {
    private static final int DEF_INT = 0x100;
   // public static final int EVENT_UPDATED_NOTIFICATION_TITLE = DEF_INT+1;
//    public static final int EVENT_UPDATED_NOTIFICATION_PUSH_CONTENT = DEF_INT+2;
//    public static final int EVENT_UPDATED_NOTIFICATION_ALARM_CLOCK_CONTENT = DEF_INT+3;
//    public static final int EVENT_UPDATED_NOTIFICATION_SCHEDULE_CONTENT = DEF_INT+4;
    /**
     * 同步手环所有数据已完成
     */
    public static final int EVENT_SYNC_DEVICE_ALL_DATA_SUCCESS = DEF_INT+5;
    /**
     * 同步手环UI部分数据已完成 用于快速UI刷新
     */
    public static final int EVENT_SYNC_DEVICE_UI_DATA_SUCCESS = DEF_INT+6;

    /**
     * 手动请求同步, 参数start true为开始同步 false 为停止
     */
    public static final int EVENT_USER_REQUEST_SYNC_DEVICE_DATA = DEF_INT+7;


    /**
     * 用户未读消息数
     */
    public static final int EVENT_USER_UNREAD_MESSAGE_NUMBER = DEF_INT+8;

    /**
     * 用户饮食数据
     */
    public static final int EVENT_SYNC_DIET_STATISTICS_DATA_SUCCESS = DEF_INT+9;
    /**
     * 同步天气 请求
     */
    public static final int EVENT_USER_REQUEST_SYNC_WEATHER_DATA = DEF_INT+10;
    /**
     * 同步天气 成功
     */
    public static final int EVENT_SYNC_WEATHER_DATA_SUCCESS = DEF_INT+11;
}
