package com.app.fmate.bean.room

import androidx.room.*
import com.app.fmate.bean.room.AppDataBase.Companion.instance
import com.shon.connector.utils.TLog
import com.google.gson.Gson

@Dao
interface RoomSleepTimeDao: BaseDao<RoomSleepTimeBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: RoomSleepTimeBean)

    @Query("select * from RoomSleepTimeBean")
    fun getAllRoomTimes():MutableList<RoomSleepTimeBean>

    @Query("select * from RoomSleepTimeBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): RoomSleepTimeBean

    @Query("select * from RoomSleepTimeBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<RoomSleepTimeBean>

    @Query("select * from RoomSleepTimeBean where dateTime  like '%' || :dateTime || '%' ")
    fun getTodayList(dateTime:String):MutableList<RoomSleepTimeBean>

    @Query("delete from RoomSleepTimeBean")
    fun deleteAll()

    @Query("delete  from RoomSleepTimeBean where id like :id")
    fun deleteID(id:Int)

     fun updateTime(startTime:Long,endTime:Long) {
        //先根据想更改的内容得到那一项的实体类,然后进行更改
        val user: RoomSleepTimeBean = getRoomTime(startTime,endTime)?:return
    TLog.error("user ++${Gson().toJson(user)}")
        user.setStartTime(startTime)
        user.setEndTime(endTime)
       // user.heart =true
        instance.getRoomSleepTimeDao()
            .update(user)
    }



}