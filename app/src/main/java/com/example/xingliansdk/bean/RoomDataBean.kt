package com.example.xingliansdk.bean

import androidx.room.*


@Entity
data class RoomDataBean (
    @PrimaryKey
    val uid: Int,
    @ColumnInfo(name = "name")
    var name: String?,
    @ColumnInfo(name = "last_name")
    var lastName: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM RoomDataBean")
    fun getAll(): List<RoomDataBean>

    @Query("SELECT * FROM RoomDataBean WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<RoomDataBean>

    @Query("SELECT * FROM RoomDataBean WHERE name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): RoomDataBean

    @Insert
    fun insertAll(vararg users: RoomDataBean)

    @Delete
    fun delete(user: RoomDataBean)
}

@Database(entities = [RoomDataBean::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}