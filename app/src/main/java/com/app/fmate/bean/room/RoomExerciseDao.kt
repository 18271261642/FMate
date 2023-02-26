package com.app.fmate.bean.room

import androidx.room.*

@Dao
interface RoomExerciseDao: BaseDao<RoomExerciseBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: RoomExerciseBean)

    @Query("select * from RoomExerciseBean")
    fun getAllRoomTimes():MutableList<RoomExerciseBean>

    @Query("select * from RoomExerciseBean where id like :id ")
    fun getRoomTime(id:Int): RoomExerciseBean

    @Query("select * from RoomExerciseBean order by time desc ")
    fun getAllByDateDesc():MutableList<RoomExerciseBean>

    @Query("delete from RoomExerciseBean")
    fun deleteAll()

    @Query("delete  from RoomExerciseBean where id like :id")
    fun deleteID(id:Int)

}