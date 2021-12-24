package com.example.xingliansdk.bean.room

import androidx.room.*
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance

@Dao
interface RoomMotionTimeDao: BaseDao<RoomMotionTimeBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: RoomMotionTimeBean)

    @Query("select * from RoomMotionTimeBean")
    fun getAllRoomTimes():MutableList<RoomMotionTimeBean>

    @Query("select * from RoomMotionTimeBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): RoomMotionTimeBean

    @Query("select * from RoomMotionTimeBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<RoomMotionTimeBean>

    @Query("delete from RoomMotionTimeBean")
    fun deleteAll()

    @Query("delete  from RoomMotionTimeBean where id like :id")
    fun deleteID(id:Int)


    //满足一天的情况
     fun updateTime(startTime:Long,endTime:Long) {
        //先根据想更改的内容得到那一项的实体类,然后进行更改
        val user: RoomMotionTimeBean = getRoomTime(startTime,endTime)?:return
        user.setStartTime(startTime)
        user.setEndTime(endTime)
        user.isMotion =true
        instance.getRoomMotionTimeDao()
            .update(user)
    }



}