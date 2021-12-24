package com.example.xingliansdk.bean.room

import androidx.room.*
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance
import com.shon.connector.utils.TLog
import com.google.gson.Gson

@Dao
interface RoomTimeDao: BaseDao<RoomTimeBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: RoomTimeBean)

    @Query("select * from RoomTimeBean")
    fun getAllRoomTimes():MutableList<RoomTimeBean>
    @Query("select * from RoomTimeBean where isBloodOxygen=1 ")
    fun getBloodOxygenTimes():MutableList<RoomTimeBean>
    @Query("select * from RoomTimeBean where isHeart=1 ")
    fun getHeartTimes():MutableList<RoomTimeBean>
    @Query("select * from RoomTimeBean where startTime = :startTime and endTime=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): RoomTimeBean

    @Query("select * from RoomTimeBean where startTime = :startTime ")
    fun getRoomTimeList(startTime:Long ): List<RoomTimeBean>

    @Query("select * from RoomTimeBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<RoomTimeBean>

    @Query("delete from RoomTimeBean")
    fun deleteAll()

    @Query("delete  from RoomTimeBean where id like :id")
    fun deleteID(id:Int)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun upgradData(element: RoomTimeBean);
//    @Update("update from RoomTimeBean where startTime = :startTime and endTime=:endTime " )
//    fun updateTime(startTime:Long,endTime:Long): RoomTimeBean
//    //根据字段查询
//    fun queryByName(startTime:Long,endTime:Long): RoomTimeBean {
//        return instance
//            .getRoomTimeDao().getRoomTime(startTime,endTime)
//    }
    //改心率满足一天的情况
     fun updateTime(startTime:Long,endTime:Long) {
        //先根据想更改的内容得到那一项的实体类,然后进行更改
        val user: RoomTimeBean = getRoomTime(startTime,endTime)
    TLog.error("user ++${Gson().toJson(user)}")
        user.setStartTime(startTime)
        user.setEndTime(endTime)
        user.heart =true
        instance.getRoomTimeDao()
            .update(user)
    }
    //改血氧满足一天的情况
    fun updateBloodOxygen(startTime:Long,endTime:Long) {
        //先根据想更改的内容得到那一项的实体类,然后进行更改
        val user: RoomTimeBean = getRoomTime(startTime,endTime)?:return
        TLog.error("blood ++${Gson().toJson(user)}")
        user.setStartTime(startTime)
        user.setEndTime(endTime)
        user.bloodOxygen =true
        instance.getRoomTimeDao()
            .update(user)
    }


}