package com.example.xingliansdk.bean.room

import androidx.room.*
import androidx.room.paging.LimitOffsetDataSource
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance
import javax.sql.DataSource

@Dao
interface WeightDao: BaseDao<WeightBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: WeightBean)

    @Query("select * from WeightBean")
    fun getAllRoomTimes():MutableList<WeightBean>

    @Query("select * from WeightBean where dateTime like '%' || :dateTime || '%'  order by time desc ")
    fun getList(dateTime:String ): MutableList<WeightBean>

    @Query("select * from WeightBean where dateTime like '%' || :dateTime || '%'")
    fun getListBean(dateTime:String ):  MutableList<WeightBean>

    @Query("select * from WeightBean where time >= :startTime and time<=:endTime order by time desc ")
    fun getTimeList(startTime:Long ,endTime:Long):  MutableList<WeightBean>

    @Query("select * from WeightBean order by time desc ")
    fun getAllByDateDesc():MutableList<WeightBean>
    @Query("select * from WeightBean order by time desc ")
    fun getPageList():LimitOffsetDataSource<WeightBean>
    @Query("delete from WeightBean")
    fun deleteAll()

    @Query("delete  from WeightBean where id like :id")
    fun deleteID(id:Int)
    @Query("delete  from WeightBean where time == :time")
    fun deleteTime(time:Long)
}