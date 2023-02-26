package com.app.fmate.bean.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomizeDialDao: BaseDao<CustomizeDialBean> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: CustomizeDialBean)

    @Query("select * from CustomizeDialBean")
    fun getAllCustomizeDialList():MutableList<CustomizeDialBean>

    @Query("select * from CustomizeDialBean order by startTime desc ")
    fun getAllByDateDesc():MutableList<CustomizeDialBean>

    @Query("delete from CustomizeDialBean")
    fun deleteAll()

    @Query("delete  from CustomizeDialBean where id like :id")
    fun deleteID(id:Int)


}