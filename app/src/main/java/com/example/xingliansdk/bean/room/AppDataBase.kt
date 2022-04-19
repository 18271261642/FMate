package com.example.xingliansdk.bean.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.bean.db.AmapSportBean
import com.example.xingliansdk.bean.db.AmapSportDao
import com.example.xingliansdk.bean.node.ItemExerciseRecordNode


@Database(
    entities = [RoomTimeBean::class, RoomMotionTimeBean::class, RoomSleepTimeBean::class, PressureTimeBean::class,
        HeartListBean::class, BloodOxygenListBean::class, SleepListBean::class,
        MotionListBean::class, RoomExerciseBean::class, PressureListBean::class,
        TempTimeBean::class, TempListBean::class, WeightBean::class,
        BloodPressureHistoryBean::class,
        ItemExerciseRecordNode::class,  //运动记录
        CustomizeDialBean::class,//自定义表盘
        AmapSportBean::class//地图轨迹
    ], version = 8
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getRoomTimeDao(): RoomTimeDao
    abstract fun getRoomSleepTimeDao(): RoomSleepTimeDao
    abstract fun getRoomMotionTimeDao(): RoomMotionTimeDao
    abstract fun getHeartDao(): HeartListDao
    abstract fun getBloodOxygenDao(): BloodOxygenListDao
    abstract fun getRoomSleepListDao(): SleepListDao
    abstract fun getRoomTempListDao(): TempListDao
    abstract fun getMotionListDao(): MotionListDao
    abstract fun getRoomExercise(): RoomExerciseDao
    abstract fun getWeightDao():WeightDao
    abstract fun getTempTimeDao():TempTimeDao
    /**
     * 获取压力时间表
     */
    abstract fun getPressureTimeDao(): PressureTimeDao

    /**
     *获取压力时间表
     */
    abstract fun getPressureListDao(): PressureListDao

    /**
     * 获取血压
     */
    abstract fun getBloodPressureHistoryDao(): BloodPressureHistoryDao

    /**
     * 运动记录
     */
    abstract  fun getItemExerciseRecordNode():RoomExerciseRecordDao

    /**
     * 自定义表盘
     */
    abstract  fun getCustomizeDialDao():CustomizeDialDao
    /**
     * 地图轨迹
     */
    abstract  fun getAmapSportDao():AmapSportDao


    companion object {
        val instance = Single.sin

    }
    private object Single {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 既然什么都没有更改，那就来个空的实现。
                database.execSQL(
                    "ALTER TABLE RoomTimeBean ADD COLUMN mList TEXT," +
                            "isAllDay boolean"
                )
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 既然什么都没有更改，那就来个空的实现。
                database.execSQL("CREATE TABLE heartListBean(heart TEXT, startTime Long,  endTime Long)")
                //创建表
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //创建表
                database.execSQL("CREATE TABLE IF NOT EXISTS PressureTimeBean (id PRIMARY KEY ,dataUnitLength INTEGER,timeInterval INTEGER, startTime Long,  endTime Long)")
                database.execSQL("CREATE TABLE IF NOT EXISTS PressureListBean (startTime PRIMARY KEY , endTime Long,pressure TEXT)")
            }
        }
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //创建表
                database.execSQL("CREATE TABLE IF NOT EXISTS BloodPressureHistoryBean( startTime Long, endTime Long, type INTEGER,systolicBloodPressure INTEGER,diastolicBloodPressure INTEGER,dateTime TEXT)")
            }
        }
        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //创建表
              //  database.execSQL("CREATE TABLE IF NOT EXISTS TempTimeBean( id PRIMARY KEY, dataUnitType INTEGER, timeInterval INTEGER,startTime INTEGER,endTime INTEGER)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `TempTimeBean` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `dataUnitType` INTEGER NOT NULL, `timeInterval` INTEGER NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL)")
            }
        }
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //升级表
                database.execSQL("CREATE TABLE IF NOT EXISTS `CustomizeDialBean` (`id` INTEGER PRIMARY KEY AUTOINCREMENT,  `startTime` INTEGER NOT NULL, `date` TEXT, `imgPath` TEXT,`color` INTEGER NOT NULL,`functionType` INTEGER NOT NULL,`locationType` INTEGER NOT NULL,`function` TEXT,`uiFeature` TEXT,`xAxis` TEXT,`yAxis` TEXT,`value` TEXT)")
            }
        }
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //升级表
                database.execSQL("CREATE TABLE IF NOT EXISTS `AmapSportBean` (`createTime` INTEGER NOT NULL, `deviceMac` TEXT, `userId` TEXT, `dayDate` TEXT, `yearMonth` TEXT, `sportType` INTEGER NOT NULL, `mapType` INTEGER NOT NULL, `currentSportTime` TEXT, `endSportTime` TEXT, `currentSteps` INTEGER NOT NULL, `distance` TEXT, `calories` TEXT, `averageSpeed` TEXT, `pace` TEXT, `heartArrayStr` TEXT, `latLonArrayStr` TEXT, PRIMARY KEY(`createTime`))")
            }
        }

//        val MIGRATION_8_9 : Migration = object : Migration(8,9){
//            override fun migrate(database: SupportSQLiteDatabase) {
//               //自定义表盘字段中添加几个字段
//                database.execSQL("ALTER TABLE CustomizeDialBean ADD COLUMN isCurrentDial boolean DEFAULT 0")
//
//                database.execSQL("ALTER TABLE CustomizeDialBean ADD COLUMN statusTxt TEXT DEFAULT 0")
//
//                database.execSQL("ALTER TABLE CustomizeDialBean ADD COLUMN progress float DEFAULT 0")
//            }
//
//        }


        //这里创建的是数据库 User 是对应数据库名称，其他所有 例如Student Teacher 都是表
        val sin: AppDataBase = Room.databaseBuilder(
            XingLianApplication.getXingLianApplication(),
            AppDataBase::class.java,
            "StartLink.db"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .addMigrations(MIGRATION_6_7)
            .addMigrations(MIGRATION_7_8)

            .allowMainThreadQueries()
            .build()
    }


}