package com.app.fmate.bean.room

import androidx.room.*

@Dao
interface MotionListDao : BaseDao<MotionListBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: MotionListBean)

    @Query("select * from MotionListBean")
    fun getAllRoomMotionList(): MutableList<MotionListBean>

    @Query("select * from MotionListBean where startTime = :startTime and endTime=:endTime ")
    fun getRoomTime(startTime: Long, endTime: Long): MotionListBean


    @Query("select * from MotionListBean where startTime >= :startTime and startTime<=:endTime ")
    fun getTimeStepList(startTime: Long, endTime: Long): List<MotionListBean>

    @Query("select * from MotionListBean order by startTime desc ")
    fun getAllByDateDesc(): MutableList<MotionListBean>

    @Query("select * from MotionListBean order by startTime asc ")
    fun getAllByDateAsc(): MutableList<MotionListBean>

    @Query("delete from MotionListBean")
    fun deleteAll()

    @Query("select * from MotionListBean where dateTime like '%' || :time || '%'")
    fun getDayStep(time: String): MotionListBean

    @Query("select * from MotionListBean where dateTime like '%' || :dateKey || '%'")
    fun getList(dateKey: String): MutableList<MotionListBean>
//
//    /**
//     * 查询七天数据
//     */
//    @Query("select * from MotionListBean where dateTime like '%' || :dateKey || '%'" )
//    fun getListWeek(dateKey: String,endTime:String): MutableList<MotionListBean>

}