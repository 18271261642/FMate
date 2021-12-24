package com.example.xingliansdk.bean.room

import androidx.room.*

@Dao
interface PressureListDao: BaseDao<PressureListBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: PressureListBean)

    @Query("select * from PressureListBean")
    fun getAllRoomHeartList():MutableList<PressureListBean>

    @Query("select * from PressureListBean where startTime = :startTime and endTime=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): PressureListBean
    @Query("select * from PressureListBean where startTime <= :startTime and endTime>=:startTime ")
    fun getPressureList(startTime:Long): PressureListBean

    @Query("select * from PressureListBean where dateTime  like '%' || :dateTime || '%'")
    fun getSomedayPressureList(dateTime:String): MutableList<PressureListBean>

    @Query("select * from PressureListBean where dateTime  like '%' || :dateTime || '%'")
    fun getSomedayPressure(dateTime:String): PressureListBean

    @Query("select * from PressureListBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<PressureListBean>

    @Query("delete from PressureListBean")
    fun deleteAll()


}