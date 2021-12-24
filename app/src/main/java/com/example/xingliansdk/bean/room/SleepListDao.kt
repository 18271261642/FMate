package com.example.xingliansdk.bean.room

import androidx.room.*
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance

@Dao
interface SleepListDao: BaseDao<SleepListBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: SleepListBean)

    @Query("select * from SleepListBean")
    fun getAllRoomTimes():MutableList<SleepListBean>

    @Query("select * from SleepListBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): SleepListBean

    @Query("select * from SleepListBean where startTime >= :startTime and endTime<=:endTime ")
    fun getList(startTime:Long,endTime:Long): MutableList<SleepListBean>
    @Query("select * from SleepListBean where dateTime like '%' || :dateTime || '%'")
    fun getList(dateTime:String ): MutableList<SleepListBean>

    @Query("select * from SleepListBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<SleepListBean>

    @Query("delete from SleepListBean")
    fun deleteAll()

    @Query("delete  from SleepListBean where startTime like :startTime")
    fun deleteID(startTime:Long)

     fun updateTime(startTime:Long,endTime:Long) {
        //先根据想更改的内容得到那一项的实体类,然后进行更改
        val user: SleepListBean = getRoomTime(startTime,endTime) ?: return
         user.startTime = startTime
         user.endTime = endTime
       // user.heart =true
        instance.getRoomSleepListDao()
            .update(user)
    }



}