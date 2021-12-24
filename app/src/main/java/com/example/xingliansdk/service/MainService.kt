//package com.example.xingliansdk.service
//
//import android.app.Service
//import android.content.Intent
//import android.content.res.Configuration
//import android.os.Handler
//import android.os.IBinder
//import android.os.Looper
//import com.example.xingliansdk.Config.database.*
//import com.example.xingliansdk.Config.eventBus.*
//import com.example.xingliansdk.R
//import com.example.xingliansdk.bean.DeviceFirmwareBean
//import com.example.xingliansdk.bean.DevicePropertiesBean
//import com.example.xingliansdk.bean.room.*
//import com.example.xingliansdk.blecontent.BleConnection
//import com.example.xingliansdk.blecontent.BleConnection.iFonConnectError
//import com.example.xingliansdk.eventbus.SNEvent
//import com.example.xingliansdk.eventbus.SNEventBus
//import com.example.xingliansdk.utils.*
//import com.example.xingliansdk.utils.CountTimer.OnCountTimerListener
//import com.shon.connector.utils.TLog.Companion.error
//import com.example.xingliansdk.view.DateUtil
//import com.google.gson.Gson
//import com.orhanobut.hawk.Hawk
//import com.shon.connector.BleWrite
//import com.shon.connector.BleWrite.*
//import com.shon.connector.Config
//import com.shon.connector.bean.DataBean
//import com.shon.connector.bean.DeviceInformationBean
//import com.shon.connector.bean.SleepBean
//import com.shon.connector.bean.TimeBean
//import com.shon.connector.utils.TLog
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import java.util.*
//
///**
// * 功能：主服务
// * 常驻，负责处理app中各种耗时杂事
// * 功能:位置定位服务,事件接收器
// */
//class MainService : Service(), OnCountTimerListener,
//    HistoryCallInterface,
//    SpecifyDailyActivitiesHistoryCallInterface,
//    SpecifySleepHistoryCallInterface,
//    SpecifyHeartRateHistoryCallInterface,
//    SpecifyBloodOxygenHistoryCallInterface,
//    SpecifyStressFatigueHistoryCallInterface, //血压
//    FirmwareInformationInterface,//3.2.1获取设备固件信息
//    DevicePropertiesInterface,//3.2.3 APP端获取设备属性信息
//    DeviceMotionInterface,//3.2.7 APP端获取设备实时运动数据
//    DoNotDisturbModeSwitchCallInterface,//设备勿扰模式开关
//    UUIDBindInterface//是否绑定
//{
//    //多久刷新一次大数据
//    var time = 1000L
//    var oneHour = 60 * 1000L
//    private var countTimer = CountTimer(oneHour, this)
//
//    //    private Call<WeatherListBean> weatherListBeanCall;
//    private var minutes: Long = 0
//
//    //    private var date: String? = null
//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }
//
//
//    private fun getInstruction() {
//        BleWrite.writeForGetFirmwareInformation(this)
//        BleWrite.writeForGetDeviceProperties(this)
//        BleWrite.writeForGetDeviceMotion(this)
//        var time: TimeBean = TimeBean()
//        val currentTime = System.currentTimeMillis()
//        time.setYear(DateUtil.getYear(currentTime))
//        time.setMonth(DateUtil.getMonth(currentTime))
//        time.setDay(DateUtil.getDay(currentTime))
//        time.setMin(DateUtil.getMinute(currentTime))
//        time.setHours(DateUtil.getHour(currentTime))
//        time.setSs(DateUtil.getSecond(currentTime))
//        error("设置时间++" + Gson().toJson(time))
//        writeTimeCall(time)
//        if (Hawk.get<Boolean>("uuid") != null && Hawk.get<Boolean>("uuid")) {
//            TLog.error("HeartRate 不是首次链接")
//            //getBigDataHistory()
//        } else {
//            TLog.error("HeartRate 首次链接")
//            settingInstructions()
//
//        }
//
//    }
//
//    private fun controlInstructions() {
////        val switchKey = Hawk.get<Int>(FIND_DEVICE_SWITCH)
////        if (switchKey != null) {
////            BleWrite.writeFindDeviceSwitchCall(switchKey.toByte())
////        }
//        val doNotDisturbModeSwitch = Hawk.get<TimeBean>(DO_NOT_DISTURB_MODE_SWITCH)
//        if (doNotDisturbModeSwitch != null) {
//            BleWrite.writeDoNotDisturbModeSwitchCall(doNotDisturbModeSwitch,this)
//        }
//        val mTurnOnScreen = Hawk.get<Int>(TURN_ON_SCREEN)
//        if (mTurnOnScreen != null) {
//            BleWrite.writeTurnOnScreenCall(mTurnOnScreen.toByte())
//        }
//        val timeBean = Hawk.get<TimeBean>(REMINDER_DRINK_WATER)
//        if (timeBean != null) {
//            BleWrite.writeReminderDrinkWaterCall(timeBean)
//        }
//        val mReminderSedentary = Hawk.get<TimeBean>(REMINDER_SEDENTARY)
//        if (mReminderSedentary != null) {
//            BleWrite.writeReminderSedentaryCall(mReminderSedentary)
//        }
//    }
//
//    private fun settingInstructions() {
//        TLog.error("settingInstructions来了")
//        controlInstructions()
//        var mDeviceInformation = Hawk.get<DeviceInformationBean>(PERSONAL_INFORMATION,
//            DeviceInformationBean(1,18,170,60,0,0,0,1,0,0,10000))
//        TLog.error("上传过去的mDeviceInformation=="+Gson().toJson(mDeviceInformation))
//        if (mDeviceInformation != null)
//            BleWrite.writeDeviceInformationCall(mDeviceInformation)
//        var mTimeList = Hawk.get<ArrayList<TimeBean>>(TIME_LIST)
//        //闹钟
//        if (!mTimeList.isNullOrEmpty()) {
//            TLog.error("mTimeList==${Gson().toJson(mTimeList)}")
//            if (mTimeList.size > 0)
//                for (i in 0 until mTimeList.size)
//                    writeAlarmClockScheduleCall(mTimeList[i])
//        }
//        //日程
//        var scheduleList = Hawk.get<ArrayList<TimeBean>>(SCHEDULE_LIST)
//        if (!scheduleList.isNullOrEmpty()) {
//            if (scheduleList.size > 0)
//                for (i in 0 until scheduleList.size)
//                    writeAlarmClockScheduleCall(scheduleList[i])
//        }
////        BleWrite.writeWeatherCall()
//        BleWrite.writeUUIDBind(HelpUtil.getAndroidId(this), this@MainService)
//       // getBigDataHistory()
//    }
//
//    private fun getBigDataHistory() {
//        Handler(Looper.getMainLooper())
//            .postDelayed({
//                if (!iFonConnectError) {
//                    BleWrite.writeBigDataHistoryCall(
//                        Config.BigData.APP_DAILY_ACTIVITIES,
//                        this@MainService
//                    )
////                    BleWrite.writeBigDataHistoryCall(
////                        Config.BigData.APP_SLEEP,
////                        this@MainService
////                    )
////                    BleWrite.writeBigDataHistoryCall(
////                        Config.BigData.APP_HEART_RATE,
////                        this@MainService
////                    )
////                    BleWrite.writeBigDataHistoryCall(
////                        Config.BigData.APP_BLOOD_OXYGEN,
////                        this@MainService
////                    )
////                   writeBigDataHistoryCall(
////                        Config.BigData.APP_STRESS_FATIGUE,
////                        this@MainService
////                    )
//                }
//            }, 500)
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        TLog.error("AppService MainService==="+ BleConnection.isServiceStatus)
////        if(!BleConnection.isServiceStatus)
////            stopService(Intent(this, MainService::class.java))
//        TLog.error("服务是否启动++"+HelpUtil.isServiceRunning(this, MainService::class.java))
//        getRoom()
//        SNEventBus.register(this)
//        countTimer.start()
//        AppDataNotifyUtil.updateNotificationTitle(
//            this, getString(R.string.app_name), "步数"
//        )
//        minutes = 0
//    }
//
//
//    /**
//     * 一分钟回调一次
//     *
//     * @param millisecond
//     */
//    override fun onCountTimerChanged(millisecond: Long) {
//        TLog.error("isServiceStatus=="+ BleConnection.isServiceStatus)
//        error("一分钟调用一次?$millisecond")
//        var address = Hawk.get<String>("address")
//        if (address.isNullOrEmpty())
//            return
//        error("是否连接++$iFonConnectError")
//        if (iFonConnectError) {
//            error("没链接的时候重连")
//            BleConnection.connectDevice(address)
//        } else {
//            error("HeartRate 链接了直接 走发送指令")
//          //  getBigDataHistory()
//        }
//        error("location++${Gson().toJson(MyLocationUtil.getMyLocation())}")
//        minutes++
//    }
//
//    /**
//     * 系统配置改变监听 这里监听的是系统语言变化
//     *
//     * @param newConfig
//     */
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        SNEventBus.unregister(this)
//        TLog.error("MainService 暂停了===")
//        countTimer.stop()
//      //  stopService(Intent(this, MainService::class.java))
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEventResult(event: SNEvent<*>) {
//        when (event.code) {
//            Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt() -> {
//                val mDataBean = event.data as DataBean
//                //                TLog.Companion.error("=="+ new Gson().toJson(mDataBean));
//                AppDataNotifyUtil.updateNotificationTitle(
//                    this,
//                    getString(R.string.app_name),
//                    "当前步数:" + mDataBean.totalSteps.toString()
//                )
//            }
//            DEVICE_CONNECT_NOTIFY -> {
//                error("链接成功返回指令")
//                countTimer.stop()
//                countTimer = CountTimer(oneHour * 30, this)
//                countTimer.start()
//                getInstruction()
//            }
//
//        }
//    }
//
//    //  var timeList=ArrayList<TimeBean>()
//    override fun HistoryCallResult(
//        key: Byte,
//        mList: ArrayList<TimeBean>
//    ) {
//        if (mList.size <= 0) return
//        mList.reverse()
//        error(
//            " mList++${key.toInt()} " +
//                    "数据==${Gson().toJson(mList)}"
//        )
//        timeList = mList
//        mList.forEach {
//            it?.let { time ->
//                when (key) {
//                    Config.BigData.DEVICE_DAILY_ACTIVITIES -> {
//                        error("访问时间++${time.startTime}")
//                        TLog.error(
//                            "${Gson().toJson(
//                                AppDataBase.instance.getMotionListDao()
//                                    .getRoomTime(time.endTime, time.startTime)
//                            )}"
//                        )
//                        //数据库修改时间
//                    //    RoomUtils.updateMovementTime(mList, this)
//                    }
//                    Config.BigData.DEVICE_SLEEP -> {
//                  //      RoomUtils.updateSleepTime(mList, this)
//                        error("DEVICE_SLEEP")
//                    }
//                    Config.BigData.DEVICE_BLOOD_OXYGEN -> {
//                        error("DEVICE_BLOOD_OXYGEN")
//                        RoomUtils.updateBloodOxygenDate(mList, this)
//
//                    }
//                    Config.BigData.DEVICE_HEART_RATE -> {
//                        error("Heart_Rate")
//                        RoomUtils.updateHeartRateData(mList, this)
//                    }
//                    Config.BigData.DEVICE_STRESS_FATIGUE->{
//                     //   RoomUtils.updatePressure(mList, this)
//                    }
//                    else -> {
//                    }
//                }
//            }
//        }
//    }
//
//    private lateinit var sDao: RoomMotionTimeDao
//    private lateinit var timeList: MutableList<TimeBean>
//    private lateinit var mMotionListDao: MotionListDao
//    private lateinit var mSleepListDao: SleepListDao
//
//    //心率的
//    private lateinit var mHeartListDao: HeartListDao
//
//    //血氧
//    private lateinit var mBloodOxygenListDao: BloodOxygenListDao
//    //压力
//    private lateinit var mPressureListDao: PressureListDao
//    private fun getRoom() {
//        //运动
//        sDao = AppDataBase.instance.getRoomMotionTimeDao()
//        mMotionListDao = AppDataBase.instance.getMotionListDao()
//        mSleepListDao = AppDataBase.instance.getRoomSleepListDao()
//        mHeartListDao = AppDataBase.instance.getHeartDao()
//        mBloodOxygenListDao = AppDataBase.instance.getBloodOxygenDao()
//        mPressureListDao=AppDataBase.instance.getPressureListDao()
//        //    timeList= sDao.getAllRoomTimes()
//
//    }
//
//    override fun SpecifyDailyActivitiesHistoryCallResult(
//        startTime: Long,
//        endTime: Long,
//        mList: ArrayList<DataBean>
//    ) {
//     //   error("你好结束时间++$startTime,  结束时间++$endTime")
//       // error("指定日常数据++${Gson().toJson(mList)}")
//        var stepList: MutableList<Int> = mutableListOf()
//        var step = 0L
//        var distanceTotal = 0L
//        var caloriesTotal = 0L
//        mList?.forEach {
//            stepList.add(it.pedometer)
//            step += it.pedometer
//            distanceTotal += it.distance
//            caloriesTotal += it.calories
//        }
//        error("每天总步数${step}")
//        error("30分钟一组的步数++${Gson().toJson(stepList)}")
//        val stepStr: String = Gson().toJson(stepList)
//        if (endTime - startTime >= MotionListBean.day) {
//            TLog.error("一天的")
//            sDao.updateTime(startTime, endTime)
//            mMotionListDao.insert(
//                MotionListBean(
//                    startTime,
//                    endTime,
//                    stepStr,
//                    step,
//                    true,
//                    DateUtil.getDate(DateUtil.YYYY_MM_DD, (startTime + Config.TIME_START) * 1000L)
//                )
//            )
//        } else {
//            TLog.error("不到一整天的")
//            mMotionListDao.insert(
//                MotionListBean(
//                    startTime,
//                    endTime,
//                    stepStr,
//                    step,
//                    false,
//                    DateUtil.getDate(DateUtil.YYYY_MM_DD, (startTime + Config.TIME_START) * 1000L)
//                )
//            )
//        }
//        //    SNEventBus.sendEvent(AppEvent.EVENT_USER_REQUEST_SYNC_DEVICE_DATA, true)
//    }
//
//    override fun SpecifyDailyActivitiesHistoryCallResult(mList: ArrayList<DataBean>) {}
//    override fun SpecifySleepHistoryCallResult(
//        startTime: Long,
//        endTime: Long,
//        mList: ArrayList<SleepBean>,
//        bean: SleepBean
//    ) {
//        TLog.error("SpecifySleepHistoryCallResult")
////        mSleepListDao.insert(
////            SleepListBean(
////                bean.startTime,
////                bean.averageHeartRate,
////                bean.maximumHeartRate,
////                bean.minimumHeartRate,
////                bean.numberOfApnea,
////                bean.endTime,
////                bean.indexOne,
////                bean.indexTwo,
////                bean.lengthOne,
////                bean.lengthTwo,
////                bean.totalApneaTime,
////                Gson().toJson(bean.getmList())  //转成String
////            )
////        )
//    }
//
//    override fun SpecifySleepHistoryCallResult(mList: ArrayList<SleepBean>) {
//
//    }
//
//    override fun SpecifyHeartRateHistoryCallResult(
//        startTime: Long,
//        endTime: Long,
//        mList: ArrayList<Int>
//    ) {
//        TLog.error("SpecifyHeartRateHistoryCallResult")
//        val name: String = Gson().toJson(mList)
//        TLog.error("HeartRate name=="+Gson().toJson(name))
//        if (endTime - startTime >= HeartListBean.day) {
//            sDao.updateTime(startTime, endTime)
//            TLog.error(" HeartRate if=="+Gson().toJson(name))
//            mHeartListDao.insert(HeartListBean(startTime, endTime, name, true,DateUtil.getDateTime(startTime)))
//        } else {
//            TLog.error(" HeartRate else startTime==$startTime  endTime +++$endTime")
//            TLog.error(" HeartRate else=="+Gson().toJson(name))
//            mHeartListDao.insert(HeartListBean(startTime, endTime, name, false,DateUtil.getDateTime(startTime)))
//        }
//    }
//
//    override fun SpecifyHeartRateHistoryCallResult(mList: ArrayList<Int>) {}
//    override fun SpecifyBloodOxygenHistoryCallResult(
//        startTime: Long,
//        endTime: Long,
//        mList: ArrayList<Int>
//    ) {
//        val name: String = Gson().toJson(mList)
//        if (endTime - startTime >= BloodOxygenListBean.day) {
//            mBloodOxygenListDao.insert(BloodOxygenListBean(startTime, endTime, name, true,DateUtil.getDateTime(startTime)))
//        } else {
//            mBloodOxygenListDao.insert(BloodOxygenListBean(startTime, endTime, name, false,DateUtil.getDateTime(startTime)))
//        }
//    }
//
//    override fun SpecifyBloodOxygenHistoryCallResult(mList: ArrayList<Int>) {}
//
//    override fun SpecifyStressFatigueHistoryCallResult(
//        startTime: Long,
//        endTime: Long,
//        mList: ArrayList<DataBean>?
//    ) {
//        val name: String = Gson().toJson(mList)
//        if (endTime - startTime >= HeartListBean.day) {
//            mPressureListDao.insert(PressureListBean(startTime, endTime, name, true))
//        } else {
//            mPressureListDao.insert(PressureListBean(startTime, endTime, name, false))
//        }
//    }
//    override fun onResult(productNumber: String?, version: String?, nowMaC: String?, mac: String?) {
//        TLog.error("productNumber+=$productNumber")
//        SNEventBus.sendEvent(
//            DEVICE_FIRMWARE,
//            DeviceFirmwareBean(productNumber, version, nowMaC, mac)
//        )
//
//    }
//
//    override fun DeviceMotionResult(mDataBean: DataBean?) {
//        SNEventBus.sendEvent(Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt(), mDataBean)
//    }
//
//    override fun DevicePropertiesResult(
//        electricity: Int,
//        mCurrentBattery: Int,
//        mDisplayBattery: Int,
//        type: Int
//    ) {
//        TLog.error("DevicePropertiesResult==$electricity")
//        Hawk.put(
//            DEVICE_ATTRIBUTE_INFORMATION,
//            DevicePropertiesBean(electricity, mCurrentBattery, mDisplayBattery, type)
//        )
//        SNEventBus.sendEvent(DEVICE_ELECTRICITY, electricity)
//
//    }
//
//    override fun UUIDBindResult(key: Int) {
//        if (key == 2) {
//            Hawk.put("uuid", true)
//        } else
//            Hawk.put("uuid", false)
//    }
//
//    override fun DoNotDisturbModeSwitchCallResult() {
//
//    }
//
//
//}
