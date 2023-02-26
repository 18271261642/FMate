package com.app.fmate.bean.room

import androidx.room.*

@Dao
interface TempTimeDao: BaseDao<TempTimeBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: TempTimeBean)

    @Query("select * from TempTimeBean")
    fun getAllRoomTimes():MutableList<TempTimeBean>

    @Query("select * from TempTimeBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): TempTimeBean

    @Query("select * from TempTimeBean where startTime = :startTime ")
    fun getRoomTimeList(startTime:Long ): List<TempTimeBean>

    @Query("select * from TempTimeBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<TempTimeBean>

    @Query("delete from TempTimeBean")
    fun deleteAll()

    @Query("delete  from TempTimeBean where id like :id")
    fun deleteID(id:Int)



}