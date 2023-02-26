package com.app.fmate.bean.room

import androidx.room.*

@Dao
interface BloodPressureHistoryDao: BaseDao<BloodPressureHistoryBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: BloodPressureHistoryBean)

    @Query("select * from BloodPressureHistoryBean")
    fun getAllList():MutableList<BloodPressureHistoryBean>

    @Query("select * from BloodPressureHistoryBean where startTime <= :startTime")
    fun getTimeBloodPressureHistory(startTime:Long): BloodPressureHistoryBean

//    @Query("select * from BloodPressureHistoryBean where startTime <= :startTime and startTime>=:endTime ")
//    fun getDayBloodPressureHistory(startTime:Long,endTime: Long): MutableList<BloodPressureHistoryBean>

    @Query("select * from BloodPressureHistoryBean where dateTime  like '%' || :dateTime || '%' order by startTime desc")
    fun getDayBloodPressureHistory(dateTime:String): MutableList<BloodPressureHistoryBean>

    @Query("select * from BloodPressureHistoryBean where dateTime= :dateTime")
    fun getBpByDateDesc(dateTime:String):MutableList<BloodPressureHistoryBean>

    @Query("select * from BloodPressureHistoryBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<BloodPressureHistoryBean>
    @Query("delete  from BloodPressureHistoryBean where startTime == :time")
    fun deleteTime(time:Long)
    @Query("delete from BloodPressureHistoryBean")
    fun deleteAll()

}