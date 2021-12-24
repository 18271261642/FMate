package com.example.xingliansdk.bean.room

import androidx.room.*
import com.example.xingliansdk.bean.node.ItemExerciseRecordNode

@Dao
interface RoomExerciseRecordDao: BaseDao<ItemExerciseRecordNode> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: ItemExerciseRecordNode)

    @Query("select * from ItemExerciseRecordNode")
    fun getAllRoomTimes():MutableList<ItemExerciseRecordNode>

    @Query("select * from ItemExerciseRecordNode where id like :id ")
    fun getRoomTime(id:Int): ItemExerciseRecordNode

    @Query("select * from ItemExerciseRecordNode order by time desc ")
    fun getAllByDateDesc():MutableList<ItemExerciseRecordNode>

    @Query("delete from ItemExerciseRecordNode")
    fun deleteAll()

    @Query("select * from ItemExerciseRecordNode where dateTime  like '%' || :dateTime || '%' ")
    fun getDateTimeList(dateTime:String):MutableList<ItemExerciseRecordNode>

    @Query("delete  from ItemExerciseRecordNode where id like :id")
    fun deleteID(id:Int)

}