package com.example.xingliansdk.blesend

import com.example.xingliansdk.view.DateUtil
import com.shon.connector.BleWrite
import com.shon.connector.bean.TimeBean

/**
 * 一个发送的整体.
 * 用于部分数据一键发送
 */
object BleSend {
    fun sendDateTime(mInference: BleWrite.TimeCallInterface)
    {
        var time = TimeBean()
        val currentTime = System.currentTimeMillis()
        time.setYear(DateUtil.getYear(currentTime))
        time.setMonth(DateUtil.getMonth(currentTime))
        time.setDay(DateUtil.getDay(currentTime))
        time.setMin(DateUtil.getMinute(currentTime))
        time.setHours(DateUtil.getHour(currentTime))
        time.setSs(DateUtil.getSecond(currentTime))
        BleWrite.writeTimeCall(time, mInference, false)
    }
    fun sendDateTime()
    {
        var time = TimeBean()
        val currentTime = System.currentTimeMillis()
        time.setYear(DateUtil.getYear(currentTime))
        time.setMonth(DateUtil.getMonth(currentTime))
        time.setDay(DateUtil.getDay(currentTime))
        time.setMin(DateUtil.getMinute(currentTime))
        time.setHours(DateUtil.getHour(currentTime))
        time.setSs(DateUtil.getSecond(currentTime))
        BleWrite.writeTimeCall(time)
    }
}