package com.app.fmate.bean.room

import androidx.room.*
import com.app.fmate.bean.room.AppDataBase.Companion.instance

@Dao
interface TempListDao: BaseDao<TempListBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: TempListBean)

    @Query("select * from TempListBean")
    fun getAllRoomTimes():MutableList<TempListBean>

    @Query("select * from TempListBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): TempListBean

    @Query("select * from TempListBean where startTime >= :startTime and endTime<=:endTime ")
    fun getList(startTime:Long,endTime:Long): MutableList<TempListBean>
    @Query("select * from TempListBean where dateTime like '%' || :dateTime || '%'")
    fun getList(dateTime:String ): MutableList<TempListBean>
    @Query("select * from TempListBean where dateTime like '%' || :dateTime || '%'")
    fun getTempBean(dateTime:String ):  TempListBean
    @Query("select * from TempListBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<TempListBean>

    @Query("delete from TempListBean")
    fun deleteAll()

    @Query("delete  from TempListBean where id like :id")
    fun deleteID(id:Int)

     fun updateTime(startTime:Long,endTime:Long) {
        //先根据想更改的内容得到那一项的实体类,然后进行更改
        val user: TempListBean = getRoomTime(startTime,endTime) ?: return
         user.startTime = startTime
         user.endTime = endTime
       // user.heart =true
        instance.getRoomTempListDao()
            .update(user)
    }



}