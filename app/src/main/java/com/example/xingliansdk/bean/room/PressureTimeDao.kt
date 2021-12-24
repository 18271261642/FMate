package com.example.xingliansdk.bean.room

import androidx.room.*

@Dao
interface PressureTimeDao: BaseDao<PressureTimeBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: PressureTimeBean)

    @Query("select * from PressureTimeBean")
    fun getAllRoomTimes():MutableList<PressureTimeBean>

    @Query("select * from PressureTimeBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): PressureTimeBean

    @Query("select * from PressureTimeBean where startTime = :startTime ")
    fun getRoomTimeList(startTime:Long ): List<PressureTimeBean>

    @Query("select * from PressureTimeBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<PressureTimeBean>

    @Query("delete from PressureTimeBean")
    fun deleteAll()

    @Query("delete  from PressureTimeBean where id like :id")
    fun deleteID(id:Int)



}