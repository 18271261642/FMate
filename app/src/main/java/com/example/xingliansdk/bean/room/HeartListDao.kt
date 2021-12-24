package com.example.xingliansdk.bean.room

import androidx.room.*

@Dao
interface HeartListDao: BaseDao<HeartListBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: HeartListBean)

    @Query("select * from HeartListBean")
    fun getAllRoomHeartList():MutableList<HeartListBean>

    @Query("select * from HeartListBean where startTime = :startTime and endTime<=:endTime ")
    fun getRoomTime(startTime:Long,endTime:Long): HeartListBean

    @Query("select * from HeartListBean where dateTime  like '%' || :dateTime || '%'")
    fun getSomedayHeartList(dateTime:String): MutableList<HeartListBean>
    @Query("select * from HeartListBean where dateTime  like '%' || :dateTime || '%'")
    fun getSomedayHeart(dateTime:String): HeartListBean
    @Query("select * from HeartListBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<HeartListBean>

    @Query("delete from HeartListBean")
    fun deleteAll()


}