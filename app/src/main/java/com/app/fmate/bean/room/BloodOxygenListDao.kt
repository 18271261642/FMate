package com.app.fmate.bean.room

import androidx.room.*

@Dao
interface BloodOxygenListDao: BaseDao<BloodOxygenListBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: BloodOxygenListBean)

    @Query("select * from BloodOxygenListBean")
    fun getAllList():MutableList<BloodOxygenListBean>

    @Query("select * from BloodOxygenListBean where startTime = :startTime and endTime<=:endTime ")
    fun getTimeList(startTime:Long,endTime:Long): BloodOxygenListBean
    @Query("select * from BloodOxygenListBean where startTime <= :startTime and endTime>=:startTime ")
    fun getBloodOxygenList(startTime:Long): BloodOxygenListBean

    @Query("select * from BloodOxygenListBean where dateTime  like '%' || :dateTime || '%'")
    fun getSomedayBloodOxygenList(dateTime:String): MutableList<BloodOxygenListBean>
    @Query("select * from BloodOxygenListBean where dateTime  like '%' || :dateTime || '%'")
    fun getSomedayBloodOxygen(dateTime:String):BloodOxygenListBean
    @Query("select * from BloodOxygenListBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<BloodOxygenListBean>

    @Query("delete from BloodOxygenListBean")
    fun deleteAll()


}