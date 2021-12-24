package com.example.xingliansdk.utils

import com.example.xingliansdk.Config
import com.orhanobut.hawk.Hawk

object HawkUtil {

 public   fun hawkDelete()
    {
        Hawk.delete(Config.database.DEVICE_OTA)
        Hawk.delete("address")
       // Hawk.delete(Config.database.USER_INFO)
        Hawk.delete(Config.database.IMG_HEAD)
        Hawk.delete(Config.database.HOME_CARD_BEAN)
        Hawk.delete(Config.database.DO_NOT_DISTURB_MODE_SWITCH)
//        Hawk.delete(Config.database.TURN_ON_SCREEN)
        Hawk.delete(Config.database.REMINDER_DRINK_WATER)
        Hawk.delete(Config.database.REMINDER_SEDENTARY)
        Hawk.delete(Config.database.HEART_RATE_ALARM)
        Hawk.delete(Config.database.INCOMING_CALL)
        Hawk.delete(Config.database.SMS)
        Hawk.delete(Config.database.OTHER)
        Hawk.delete(Config.database.SET_TIME)
        Hawk.delete(Config.database.PERSONAL_INFORMATION)
        Hawk.delete(Config.database.SLEEP_GOAL)
        Hawk.delete(Config.database.TIME_LIST)
        Hawk.delete(Config.database.SCHEDULE_LIST)
        Hawk.delete(Config.database.WEATHER)
        Hawk.delete(Config.database.REMIND_TAKE_MEDICINE)
        Hawk.delete(Config.database.DEVICE_ATTRIBUTE_INFORMATION)
        Hawk.delete(Config.database.MESSAGE_CALL)
        Hawk.delete("first")
        Hawk.delete(Config.database.ALARM_CLOCK_CREATE_TIME)
        Hawk.delete(Config.database.SCHEDULE_CREATE_TIME)
        Hawk.delete(Config.database.TAKE_MEDICINE_CREATE_TIME)

    }

}