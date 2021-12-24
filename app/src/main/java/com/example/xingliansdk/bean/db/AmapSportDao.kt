package com.example.xingliansdk.bean.db

import androidx.room.*
import com.example.xingliansdk.bean.room.BaseDao

@Dao
interface AmapSportDao: BaseDao<AmapSportBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: AmapSportBean)

    @Query("select * from AmapSportBean")
    fun getAllList():MutableList<AmapSportBean>

    @Query("select * from AmapSportBean where sportType = :type")
    fun getRoomTime(type:Int): MutableList<AmapSportBean>

    @Query("select * from AmapSportBean where yearMonth  like '%' || :dateTime || '%'")
    fun getMonthDayList(dateTime:String): MutableList<AmapSportBean>
    @Query("select * from AmapSportBean where endSportTime  like '%' || :dateTime || '%'")
    fun getSomeday(dateTime:String): AmapSportBean
    @Query("select * from AmapSportBean order by createTime desc ")
    fun getAllByDateDesc():MutableList<AmapSportBean>

    @Query("delete from AmapSportBean")
    fun deleteAll()


}