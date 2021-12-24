package com.example.xingliansdk.utils

import com.example.xingliansdk.Config.database.DEVICE_OTA
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.bean.room.*
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.BleWrite.SpecifyDailyActivitiesHistoryCallInterface
import com.shon.connector.Config
import com.shon.connector.bean.TimeBean
import com.shon.connector.utils.TLog

object RoomUtils {
    /**
     * 删除room所有数据库
     */
    fun roomDeleteAll() {
        val mRoomTimeDao = instance.getRoomTimeDao() //时间表
        val mHeartListDao = instance.getHeartDao()//心率
        val mSleepListDao = instance.getRoomSleepListDao() //睡眠
        val mRoomSleepTimeDao = instance.getRoomSleepTimeDao() //睡眠时间表吧
        val mBloodOxygenListDao = instance.getBloodOxygenDao() //血氧
        val mMapSportDao = instance.getAmapSportDao()
//        instance.getRoomMotionTimeDao().deleteAll() //步数
//        instance.getBloodPressureHistoryDao().deleteAll() //血压
//        instance.getItemExerciseRecordNode().deleteAll() //运动记录
//        instance.getMotionListDao().deleteAll() //手表运动记录
//        instance.getPressureListDao().deleteAll()
//        instance.getPressureTimeDao().deleteAll()
//        instance.getRoomExercise().deleteAll()
//        instance.getRoomTempListDao().deleteAll()
//        instance.getWeightDao().deleteAll()

//        mBloodOxygenListDao.deleteAll()
//        mRoomSleepTimeDao.deleteAll()
//        mSleepListDao.deleteAll()
//        mRoomTimeDao.deleteAll()
//        mHeartListDao.deleteAll()
        // mMapSportDao.deleteAll()
        instance.clearAllTables()
        // instance.close()
        HawkUtil.hawkDelete()
    }


    fun updateBloodOxygenDate(
        allData: ArrayList<TimeBean>,
        mInterface: BleWrite.SpecifyBloodOxygenHistoryCallInterface
    ) {
        var sDao: RoomTimeDao = instance.getRoomTimeDao()
        val allRoomTimes = sDao.getAllRoomTimes()
        allData.forEach { timeBean ->
            val find = allRoomTimes.find { roomTime ->
                timeBean.startTime == roomTime.startTime
                        && roomTime.bloodOxygen
            }
            if (find == null) {
                sDao.insert(
                    RoomTimeBean(
                        timeBean.dataUnitType,
                        timeBean.timeInterval,
                        timeBean.startTime,
                        timeBean.endTime,
                        false, true
                    )
                )
//                TLog.error("添加血氧")
                BleWrite.writeSpecifyBloodOxygenHistoryCall(
                    timeBean.startTime,
                    timeBean.endTime,
                    mInterface
                )

            } else {
                if (timeBean.endTime != find.endTime) {
                    TLog.error("修改血氧")
                    find.startTime = timeBean.startTime
                    find.endTime = timeBean.endTime
                    sDao.update(find)
                    //               TLog.error("timeBean修改" + timeBean.endTime)
//                    TLog.error("find++" + find.endTime)
                    BleWrite.writeSpecifyBloodOxygenHistoryCall(
                        timeBean.startTime,
                        timeBean.endTime,
                        mInterface
                    )
                }
            }
        }
        //  TLog.error("sDao++${Gson().toJson(sDao.getAllRoomTimes())}")
    }

    public fun updateHeartRateData(
        allData: ArrayList<TimeBean>,
        mInterface: BleWrite.SpecifyHeartRateHistoryCallInterface
    ) {
        TLog.error("Heart_Rate++" + Gson().toJson(allData))
        var sDao: RoomTimeDao = instance.getRoomTimeDao()
        val allRoomTimes = sDao.getAllRoomTimes()
        TLog.error("Heart_Rate HeartRatelist=" + Gson().toJson(allRoomTimes))
        allData.forEach { timeBean ->
            val find = allRoomTimes.find { roomTime ->
                timeBean.startTime == roomTime.startTime
                        && roomTime.heart
            }
            //find 就是数据库中的数据
            if (find == null) {
                //不在数据库，插入新的数据
                sDao.insert(
                    RoomTimeBean(
                        timeBean.dataUnitType,
                        timeBean.timeInterval,
                        timeBean.startTime,
                        timeBean.endTime,
                        true, false
                    )
                )
                BleWrite.writeSpecifyHeartRateHistoryCall(
                    timeBean.startTime,
                    timeBean.endTime,
                    mInterface
                )

            } else {
                //这里已经存在
//                TLog.error("这里已经存在=="+timeBean.endTime)
//                TLog.error("这里已经存在=="+find.endTime)
                if (timeBean.endTime != find.endTime) {
                    //结束时间不相同，更新数据库
                    //赋值新数据
                    find.startTime = timeBean.startTime
                    find.endTime = timeBean.endTime
                    sDao.update(find)
                    TLog.error("Heart_Rate timeBean修改" + timeBean.endTime)
                    TLog.error("Heart_Rate find++" + Gson().toJson(find))
                    BleWrite.writeSpecifyHeartRateHistoryCall(
                        timeBean.startTime,
                        timeBean.endTime,
                        mInterface
                    )
                }
            }
        }
//        TLog.error("sDao++${Gson().toJson(sDao.getAllRoomTimes())}")
    }

    public fun updateMovementTime(
        allData: ArrayList<TimeBean>,
        mInterface: SpecifyDailyActivitiesHistoryCallInterface
    ) {
        TLog.error("updateMovementTime ++" + Gson().toJson(allData))
        var sDao: RoomMotionTimeDao = instance.getRoomMotionTimeDao()
        val allRoomTimes = sDao.getAllRoomTimes()
        allData.forEach { timeBean ->
            val find = allRoomTimes.find { roomTime ->
                timeBean.startTime == roomTime.startTime
            }
            if (find == null) {
//                TLog.error("updateMovementTime 添加")
                sDao.insert(
                    RoomMotionTimeBean(
                        timeBean.dataUnitType,
                        timeBean.timeInterval,
                        timeBean.startTime,
                        timeBean.endTime
                    )
                )
                BleWrite.writeSpecifyDailyActivitiesHistoryCall(
                    timeBean.startTime, timeBean.endTime,
                    mInterface
                )
            } else {
                if (timeBean.endTime != find.endTime) {
                    find.startTime = timeBean.startTime
                    find.endTime = timeBean.endTime
                    sDao.update(find)
                    BleWrite.writeSpecifyDailyActivitiesHistoryCall(
                        timeBean.startTime, timeBean.endTime,
                        mInterface
                    )
                    TLog.error("updateMovementTime timeBean修改" + timeBean.endTime)
                    TLog.error("updateMovementTime find++" + find.endTime)
                }
            }
        }
        //  TLog.error("新的数据库  size+ ${sDao.getAllRoomTimes().size} 数据 +${Gson().toJson(sDao.getAllRoomTimes())}")
    }

    fun updateSleepTime(
        allData: ArrayList<TimeBean>,
        mInterface: BleWrite.SpecifySleepHistoryCallInterface
    ) {
        var sDao: RoomSleepTimeDao = instance.getRoomSleepTimeDao()
        // TLog.error("updateData++" + Gson().toJson(allData))
        val allRoomTimes = sDao.getAllRoomTimes()
        allData.forEach { timeBean ->
            val find = allRoomTimes.find { roomTime ->
                timeBean.startTime == roomTime.startTime
            }
            if (find == null) {
                TLog.error("HomeFragment  睡眠没有添加操作")
                sDao.insert(
                    RoomSleepTimeBean(
                        timeBean.dataUnitType,
                        timeBean.timeInterval,
                        timeBean.startTime,
                        timeBean.endTime,
                        DateUtil.getDate(
                            DateUtil.YYYY_MM_DD, (timeBean.endTime + Config.TIME_START) * 1000L
                        )
                    )
                )
                BleWrite.writeSpecifySleepHistoryCall(
                    timeBean.startTime, timeBean.endTime,
                    mInterface
                )
            } else {
                if (timeBean.endTime != find.endTime) {
                    find.startTime = timeBean.startTime
                    find.endTime = timeBean.endTime
                    sDao.update(find)
                }
//                TLog.error("HomeFragment  睡眠有修改操作")
                BleWrite.writeSpecifySleepHistoryCall(
                    timeBean.startTime, timeBean.endTime,
                    mInterface
                )
            }
        }
        // TLog.error("sDao++${Gson().toJson(sDao.getAllRoomTimes())}")
    }


    fun updatePressure(
        allData: ArrayList<TimeBean>,
        mInterface: BleWrite.SpecifyStressFatigueHistoryCallInterface
    ) {
        TLog.error("压力++" + Gson().toJson(allData))
        var sDao: PressureTimeDao = instance.getPressureTimeDao()
        val allRoomTimes = sDao.getAllRoomTimes()
        allData.forEach { timeBean ->
            val find = allRoomTimes.find { roomTime ->
                timeBean.startTime == roomTime.startTime
            }
            //find 就是数据库中的数据
            if (find == null) {
                //不在数据库，插入新的数据
                sDao.insert(
                    PressureTimeBean(
                        timeBean.dataUnitType,
                        timeBean.timeInterval,
                        timeBean.startTime,
                        timeBean.endTime
                    )
                )
                BleWrite.writeSpecifyStressFatigueHistoryCall(
                    timeBean.startTime,
                    timeBean.endTime,
                    mInterface
                )

            } else {
                //这里已经存在
                // TLog.error("这里已经存在")
                if (timeBean.endTime != find.endTime) {
                    //结束时间不相同，更新数据库
                    //赋值新数据
                    find.startTime = timeBean.startTime
                    find.endTime = timeBean.endTime
                    sDao.update(find)
                    //      TLog.error("timeBean修改" + timeBean.endTime)
                    //       TLog.error("find++" + find.endTime)
                    BleWrite.writeSpecifyStressFatigueHistoryCall(
                        timeBean.startTime,
                        timeBean.endTime,
                        mInterface
                    )
                }
            }
        }
        //  TLog.error("sDao++${Gson().toJson(sDao.getAllRoomTimes())}")
    }

    fun updateTemp(
        allData: ArrayList<TimeBean>,
        mInterface: BleWrite.SpecifyTemperatureHistoryCallInterface
    ) {
        TLog.error("HeartRate++" + Gson().toJson(allData))
        var sDao: TempTimeDao = instance.getTempTimeDao()
        val mList = sDao.getAllRoomTimes()
        allData.forEach { timeBean ->
            val find = mList.find { roomTime ->
                timeBean.startTime == roomTime.startTime
                timeBean.endTime == roomTime.endTime
            }
//            TLog.error("测试+find+"+Gson().toJson(find))
            //find 就是数据库中的数据
            if (find == null) {
                //不在数据库，插入新的数据

                sDao.insert(
                    TempTimeBean(
                        timeBean.dataUnitType,
                        timeBean.timeInterval,
                        timeBean.startTime,
                        timeBean.endTime
                    )
                )

                BleWrite.writeSpecifyTemperatureHistoryCall(
                    timeBean.startTime,
                    timeBean.endTime,
                    mInterface
                )
                TLog.error(
                    "不存在的+${
                        Gson().toJson(
                            sDao.getRoomTime(
                                timeBean.startTime,
                                timeBean.endTime
                            )
                        )
                    }"
                )
            } else {
                //这里已经存在
                // TLog.error("这里已经存在")
                if (timeBean.endTime != find.endTime) {
                    //结束时间不相同，更新数据库
                    //赋值新数据
                    find.startTime = timeBean.startTime
                    find.endTime = timeBean.endTime
                    sDao.update(find)
                    //      TLog.error("timeBean修改" + timeBean.endTime)
                    //       TLog.error("find++" + find.endTime)
                    BleWrite.writeSpecifyTemperatureHistoryCall(
                        timeBean.startTime,
                        timeBean.endTime,
                        mInterface
                    )
                }
            }
        }
        //  TLog.error("sDao++${Gson().toJson(sDao.getAllRoomTimes())}")
    }
}