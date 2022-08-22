package com.example.xingliansdk.service.work

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.xingliansdk.Config.database.*
import com.example.xingliansdk.Config.eventBus.*
import com.example.xingliansdk.MainHomeActivity
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.bean.DeviceFirmwareBean
import com.example.xingliansdk.bean.DevicePropertiesBean
import com.example.xingliansdk.bean.room.*
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.blecontent.BleConnection.iFonConnectError
import com.example.xingliansdk.blesend.BleSend
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.RecommendDialViewApi
import com.example.xingliansdk.network.api.homeView.HomeViewApi
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.setAllClock.AlarmClockBean
import com.example.xingliansdk.network.api.setAllClock.SetAllClockApi
import com.example.xingliansdk.service.core.IWork
import com.example.xingliansdk.ui.setting.MyDeviceActivity
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.utils.CountTimer.OnCountTimerListener
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.HawConstant
import com.shon.connector.BleWrite
import com.shon.connector.BleWrite.*
import com.shon.connector.Config
import com.shon.connector.bean.*
import com.shon.connector.call.listener.CommBackListener
import com.shon.connector.call.write.dial.DialGetAssignCall
import com.shon.connector.utils.TLog
import com.shon.connector.utils.TLog.Companion.error
import kotlinx.android.synthetic.main.activity_my_device.*
import kotlinx.android.synthetic.main.item_switch.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * 功能：主服务
 * 常驻，负责处理app中各种耗时杂事
 * 功能:位置定位服务,事件接收器
 */
class BleWork : IWork, OnCountTimerListener,
    HistoryCallInterface,
    SpecifyDailyActivitiesHistoryCallInterface,
    SpecifySleepHistoryCallInterface,
    SpecifyHeartRateHistoryCallInterface,
    SpecifyBloodOxygenHistoryCallInterface,
    SpecifyStressFatigueHistoryCallInterface, //血压
    SpecifyTemperatureHistoryCallInterface,
    FirmwareInformationInterface,//3.2.1获取设备固件信息
    DevicePropertiesInterface,//3.2.3 APP端获取设备属性信息
    DeviceMotionInterface,//3.2.7 APP端获取设备实时运动数据
    DoNotDisturbModeSwitchCallInterface,//设备勿扰模式开关
    UUIDBindInterface,//是否绑定
    TimeCallInterface,//首次设置时间
    FlashGetDialInterface,
    LocationServiceHelper.OnLocationListener {
    //多久刷新一次大数据
    var time = 1000L
    var oneHour = 30 * 1000L
    private var countTimer = CountTimer(oneHour, this)
    private var minutes: Long = 0
    private lateinit var context: Context
    var bigDataHistoryStatus = false
    var userInfo: LoginBean = Hawk.get(USER_INFO, LoginBean())


    private var mLSHelper: LocationServiceHelper? = null
    override fun init(context: Context) {
        super.init(context)
        this.context = context
        mLSHelper = LocationServiceHelper(context)
        mLSHelper?.setOnLocationListener(this)
        getRoom()
        countTimer.start()
        minutes = 0
        SNEventBus.register(this)
    }

    public fun startLocation() {
        //重复定位 大于1小时的,重新定位
        if (mLSHelper != null) {
            mLSHelper!!.startLocation()
        }
    }

    public fun startLocation(context: Context) {
        //重复定位 大于1小时的,重新定位
        if(mLSHelper == null){
            mLSHelper = LocationServiceHelper(context)
            mLSHelper?.setOnLocationListener(this)
            mLSHelper!!.startLocation()
        }else{
            if (mLSHelper != null) {
                mLSHelper!!.startLocation()
            }
        }


    }

    /**
     * 一分钟回调一次
     *
     * @param millisecond
     */
    override fun onCountTimerChanged(millisecond: Long) {
//        error("一分钟调用一次?$millisecond")
        var address = Hawk.get<String>("address")
        if (address.isNullOrEmpty())
            return
//        error("是否连接++$iFonConnectError")
        if (iFonConnectError) {
            error("后台 没链接的时候重连")
            BleConnection.initStart(Hawk.get(DEVICE_OTA, false), 5000)
            //    BleConnection.connectDevice(address)
        } else {
            if (bigDataHistoryStatus) //必须首页回调过
            {
                if (MyDeviceActivity.FlashBean.UIFlash) {
                    TLog.error("HomeFragment 首页 发送过后 的回调 拿去过往数据")
                    getBigDataHistory()
                }
            }
        }
        minutes++
    }

    //最先发出去的数据
    private fun getInstruction() {
        BleSend.sendDateTime(this)

        BleWrite.writeForGetDeviceProperties(this, false)
        var mDeviceInformation = Hawk.get(
            PERSONAL_INFORMATION,
            DeviceInformationBean(2, 18, 160, 50f, 0, 0, 1, 0, 0, 0, 10000)
        )


        Log.e("连接成功设置设备信息", "-------mDeviceInformation=$mDeviceInformation")


        BleWrite.writeDeviceInformationCall(mDeviceInformation, false)
        BleWrite.writeForGetFirmwareInformation(this, false)
        BleWrite.writeForGetDeviceMotion(this, false)
        BleWrite.writeUUIDBind(HelpUtil.getAndroidId(context), this@BleWork)
        BleWrite.writeFlashGetDialCall(this)
        startLocation()
        error("HomeFragment getInstruction")
    }

    private fun controlInstructions() {
        // notDisturb()  //勿扰模式被阉割

        //  Hawk.put(TURN_ON_SCREEN, Hawk.get(TURN_ON_SCREEN, 2))
        writeTurnOnScreenCall(userInfo.userConfig.turnScreen.toInt().toByte())
        drinkWater()
        sedentary()

        setNightMeasureBp()

    }

    //设置夜间血压测量状态
    private fun setNightMeasureBp(){
       val autoBpStatusBean = AutoBpStatusBean()
        //白天的间隔

        var userInfos: LoginBean = Hawk.get(USER_INFO, LoginBean())


       autoBpStatusBean.startHour = 0x08
        autoBpStatusBean.startMinute = 0x00
        autoBpStatusBean.endHour = 0x17
        autoBpStatusBean.endMinute = 0x3B
        autoBpStatusBean.bpInterval = 0x1E
        autoBpStatusBean.normalBpStatus = userInfos.userConfig.bloodPressureDaytimeMeasurement.toByte()
        autoBpStatusBean.nightBpStatus = userInfos.userConfig.bloodPressureNightMeasurement.toByte()


        TLog.error("----------网络夜间血压="+autoBpStatusBean.toString())

        BleWrite.writeSetAutoBpMeasureStatus(true,autoBpStatusBean
        ) { }

    }

    private fun sedentary() {
        val sedentaryTimeBean = TimeBean()
        sedentaryTimeBean.switch = userInfo.userConfig.sedentary.toInt()
        sedentaryTimeBean.specifiedTime = 127
        sedentaryTimeBean.openHour = 8
        sedentaryTimeBean.openMin = 0
        sedentaryTimeBean.closeHour = 22
        sedentaryTimeBean.closeMin = 0
        sedentaryTimeBean.reminderInterval = 60
        var sedentary = Hawk.get<TimeBean>(REMINDER_SEDENTARY)
        if (sedentary == null) {
            Hawk.put(REMINDER_SEDENTARY, sedentaryTimeBean)
        } else
            sedentary = Hawk.get(REMINDER_SEDENTARY, sedentaryTimeBean)
        writeReminderSedentaryCall(Hawk.get(REMINDER_SEDENTARY, sedentary))
    }

    private fun notDisturb() {
        val notDisturbTimeBean = TimeBean()
        notDisturbTimeBean.switch = userInfo.userConfig.doNotDisturb.toInt()
        notDisturbTimeBean.openHour = 22
        notDisturbTimeBean.openMin = 0
        notDisturbTimeBean.closeHour = 7
        notDisturbTimeBean.closeMin = 0
        var notDisturb = Hawk.get<TimeBean>(DO_NOT_DISTURB_MODE_SWITCH)
        if (notDisturb == null) {
            Hawk.put(DO_NOT_DISTURB_MODE_SWITCH, notDisturbTimeBean)
        } else
            notDisturb = Hawk.get(DO_NOT_DISTURB_MODE_SWITCH, notDisturbTimeBean)
        writeDoNotDisturbModeSwitchCall(Hawk.get(DO_NOT_DISTURB_MODE_SWITCH, notDisturb), this)
    }

    private fun drinkWater() {
        val drinkWaterTimeBean = TimeBean()
        drinkWaterTimeBean.switch = userInfo.userConfig.drinkWater.toInt()
        drinkWaterTimeBean.specifiedTime = 127
        drinkWaterTimeBean.openHour = 8
        drinkWaterTimeBean.openMin = 0
        drinkWaterTimeBean.closeHour = 22
        drinkWaterTimeBean.closeMin = 0
        drinkWaterTimeBean.reminderInterval = 60
        var drinkWater = Hawk.get<TimeBean>(REMINDER_DRINK_WATER)
        if (drinkWater == null) {
            Hawk.put(REMINDER_DRINK_WATER, drinkWaterTimeBean)
        } else
            drinkWater = Hawk.get(REMINDER_DRINK_WATER, drinkWaterTimeBean)
        writeReminderDrinkWaterCall(Hawk.get(REMINDER_DRINK_WATER, drinkWater))
    }

    private fun settingInstructions() {
        controlInstructions()
        BleWrite.writeHeartRateAlarmSwitchCall(
            userInfo.userConfig.heartRateAlarm.toDouble().toInt(),
            userInfo.userConfig.heartRateThreshold
        )
        ///////闹钟//日程
        getAllClock()
        writeSettingUID()
    }


    private fun getBigDataHistory() {
        TLog.error("getBigDataHistory 被访问")
        Handler(Looper.getMainLooper())
            .postDelayed({
                if (!iFonConnectError) {
                    BleWrite.writeBigDataHistoryCall(
                        Config.BigData.APP_DAILY_ACTIVITIES,
                        this@BleWork
                    )
                    BleWrite.writeBigDataHistoryCall(
                        Config.BigData.APP_SLEEP,
                        this@BleWork
                    )
                    BleWrite.writeBigDataHistoryCall(
                        Config.BigData.APP_HEART_RATE,
                        this@BleWork
                    )
                    BleWrite.writeBigDataHistoryCall(
                        Config.BigData.APP_BLOOD_OXYGEN,
                        this@BleWork
                    )
                    writeBigDataHistoryCall(
                        Config.BigData.APP_STRESS_FATIGUE,
                        this@BleWork
                    )
                    writeBigDataHistoryCall(
                        Config.BigData.APP_TEMPERATURE,
                        this@BleWork
                    )
                }
            }, 500)
    }


    override fun destroy() {
        super.destroy()
        countTimer.stop()
        SNEventBus.unregister(this)
    }

    override fun onReceivedMessage(event: SNEvent<*>) {
        when (event.code) {
            Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt() -> {
                val mDataBean = event.data as DataBean
                //                TLog.Companion.error("=="+ new Gson().toJson(mDataBean));
                AppDataNotifyUtil.updateNotificationTitle(
                    context,
                    context.resources.getString(R.string.app_name),
                    "当前步数:" + mDataBean.totalSteps.toString()
                )
            }
            DEVICE_CONNECT_NOTIFY -> {
                error("链接成功返回指令")
                countTimer.stop()
                countTimer = CountTimer(oneHour * 30, this)
                countTimer.start()
                getInstruction()
            }

        }
    }

    //  var timeList=ArrayList<TimeBean>()
    override fun HistoryCallResult(
        key: Byte,
        mList: ArrayList<TimeBean>
    ) {
        if (mList.size <= 0) return
        mList.reverse()
//        error(
//            " mList++${key.toInt()} " +
//                    "数据==${Gson().toJson(mList)}"
//        )
        timeList = mList
        mList?.let { time ->
            when (key) {
                Config.BigData.DEVICE_DAILY_ACTIVITIES -> {
//                        error("访问时间++${time.startTime}")
//                        TLog.error(
//                            "${Gson().toJson(
//                                AppDataBase.instance.getMotionListDao()
//                                    .getRoomTime(time.endTime, time.startTime)
//                            )}"
//                        )
                    //数据库修改时间
                    RoomUtils.updateMovementTime(mList, this)
                }
                Config.BigData.DEVICE_SLEEP -> {
                    RoomUtils.updateSleepTime(mList, this)
                    error("DEVICE_SLEEP==")
                }
                Config.BigData.DEVICE_BLOOD_OXYGEN -> {
                    error("血氧  DEVICE_BLOOD_OXYGEN=" + Gson().toJson(mList))
                    RoomUtils.updateBloodOxygenDate(mList, this)

                }
                Config.BigData.DEVICE_HEART_RATE -> {
                    error("Heart_Rate==${Gson().toJson(mList)}")

//                    if(mList.size>1)
//                        mList[mList.size-2].endTime= mList[mList.size-2].endTime-7200

                    error("Heart_Rate==${Gson().toJson(mList)}")
                    RoomUtils.updateHeartRateData(mList, this)
                }
                Config.BigData.DEVICE_STRESS_FATIGUE -> {
                    TLog.error("压力值")
                    RoomUtils.updatePressure(mList, this)
                }
                Config.BigData.DEVICE_TEMPERATURE -> {
                    TLog.error("体温" + Gson().toJson(mList))

                    RoomUtils.updateTemp(mList, this)
                }
                else -> {
                }
            }
        }
    }

    private lateinit var sDao: RoomMotionTimeDao
    private lateinit var timeList: MutableList<TimeBean>
    private lateinit var mMotionListDao: MotionListDao
    private lateinit var mSleepListDao: SleepListDao

    //心率的
    private lateinit var mHeartListDao: HeartListDao

    //血氧
    private lateinit var mBloodOxygenListDao: BloodOxygenListDao

    //压力
    private lateinit var mPressureListDao: PressureListDao
    private lateinit var mTempListDao: TempListDao
    private fun getRoom() {
        //运动
        sDao = AppDataBase.instance.getRoomMotionTimeDao()
        mMotionListDao = AppDataBase.instance.getMotionListDao()
        mSleepListDao = AppDataBase.instance.getRoomSleepListDao()
        mHeartListDao = AppDataBase.instance.getHeartDao()
        mBloodOxygenListDao = AppDataBase.instance.getBloodOxygenDao()
        mPressureListDao = AppDataBase.instance.getPressureListDao()
        mTempListDao = AppDataBase.instance.getRoomTempListDao()
        //    timeList= sDao.getAllRoomTimes()

    }

    override fun SpecifyDailyActivitiesHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<DailyActiveBean>
    ) {
        //   error("你好结束时间++$startTime,  结束时间++$endTime")
        // error("指定日常数据++${Gson().toJson(mList)}")
        var stepList: MutableList<Int> = mutableListOf()
        var step = 0L
        var distanceTotal = 0L
        var caloriesTotal = 0L
        mList?.forEach {
            stepList.add(it.steps)
            step += it.steps
            distanceTotal += it.distance
            caloriesTotal += it.calorie
        }
        //        error("30分钟一组的步数++${Gson().toJson(stepList)}")
        val stepStr: String = Gson().toJson(stepList)
        if (endTime - startTime >= MotionListBean.day) {
            sDao.updateTime(startTime, endTime)
            mMotionListDao.insert(
                MotionListBean(
                    DateUtil.getLongTime(startTime),
                    DateUtil.getLongTime(endTime),
                    stepStr,
                    step,
                    true,
                    DateUtil.getDate(DateUtil.YYYY_MM_DD, (startTime + Config.TIME_START) * 1000L)
                )
            )
        } else {
            mMotionListDao.insert(
                MotionListBean(
                    DateUtil.getLongTime(startTime),
                    DateUtil.getLongTime(endTime),
                    stepStr,
                    step,
                    false,
                    DateUtil.getDate(DateUtil.YYYY_MM_DD, (startTime + Config.TIME_START) * 1000L)
                )
            )
        }
//        TLog.error("运动大数据++${DateUtil.getLongTime(startTime)}++" + Gson().toJson(mList))
        getDaily(
            DateUtil.getLongTime(startTime),
            DateUtil.getLongTime(endTime), Gson().toJson(mList)
        )
    }

    override fun SpecifyDailyActivitiesHistoryCallResult(mList: ArrayList<DailyActiveBean>) {}
    override fun SpecifySleepHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<SleepBean>,
        bean: SleepBean
    ) {
        TLog.error("SpecifySleepHistoryCallResult")
        bean.getmList()
        mSleepListDao.insert(
            SleepListBean(
                bean.startTime + XingLianApplication.TIME_START,
                bean.averageHeartRate,
                bean.maximumHeartRate,
                bean.minimumHeartRate,
                bean.numberOfApnea,
                bean.endTime + XingLianApplication.TIME_START,
                bean.indexOne,
                bean.indexTwo,
                bean.lengthOne,
                bean.lengthTwo,
                bean.totalApneaTime,
                bean.respiratoryQuality,
                Gson().toJson(bean.getmList())  //转成String
                , DateUtil.getDate(DateUtil.YYYY_MM_DD, (endTime + Config.TIME_START) * 1000L)
            )
        )
        getSleep(bean)
    }

    override fun SpecifySleepHistoryCallResult(mList: ArrayList<SleepBean>) {

    }

    override fun SpecifyHeartRateHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<Int>
    ) {
        val name: String = Gson().toJson(mList)
        TLog.error("RetrofitLog Heart_Rate 心率大数据 ==name+" + name)
        // TLog.error("RetrofitLog Heart_Rate 心率大数据 ==startTime+" + (XingLianApplication.TIME_START + startTime) + "====endTime++" + (XingLianApplication.TIME_START + endTime))

        getHeart(
            (XingLianApplication.TIME_START + startTime),
            (XingLianApplication.TIME_START + endTime),
            mList
        )

        if (endTime - startTime >= HeartListBean.day) {
            sDao.updateTime(startTime, endTime)
            mHeartListDao.insert(
                HeartListBean(
                    XingLianApplication.TIME_START + startTime,
                    XingLianApplication.TIME_START + endTime,
                    name,
                    true,
                    DateUtil.getDateTime(startTime)
                )
            )
        } else {
            mHeartListDao.insert(
                HeartListBean(
                    XingLianApplication.TIME_START + startTime,
                    XingLianApplication.TIME_START + endTime,
                    name,
                    false,
                    DateUtil.getDateTime(startTime)
                )
            )
        }
    }

    override fun SpecifyHeartRateHistoryCallResult(mList: ArrayList<Int>) {}
    override fun SpecifyBloodOxygenHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<Int>
    ) {
        //   TLog.error("血氧++" + startTime)
        //    TLog.error("血氧++" + endTime)
        //    TLog.error("血氧++" + (endTime - startTime))
        val name: String = Gson().toJson(mList)
//        TLog.error("血氧++" + name)
        getBloodOxygen(
            startTime + XingLianApplication.TIME_START,
            endTime + XingLianApplication.TIME_START,
            mList
        )
        if (endTime - startTime >= BloodOxygenListBean.day) {
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    startTime + XingLianApplication.TIME_START,
                    endTime + XingLianApplication.TIME_START,
                    name,
                    true,
                    DateUtil.getDateTime(
                        startTime
                    )
                )
            )
        } else {
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    startTime + XingLianApplication.TIME_START,
                    endTime + XingLianApplication.TIME_START,
                    name,
                    false,
                    DateUtil.getDateTime(
                        startTime
                    )
                )
            )
        }
    }

    override fun SpecifyBloodOxygenHistoryCallResult(mList: ArrayList<Int>) {}

    override fun SpecifyStressFatigueHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<PressureBean>
    ) {
        val name: String = Gson().toJson(mList)
        getPressure(
            DateUtil.getLongTime(startTime),
            DateUtil.getLongTime(endTime), mList
        )
        if (endTime - startTime >= HeartListBean.day) {
            mPressureListDao.insert(
                PressureListBean(
                    DateUtil.getLongTime(startTime),
                    DateUtil.getLongTime(endTime),
                    name,
                    true,
                    DateUtil.getDateTime(
                        startTime
                    )
                )
            )
        } else {
            mPressureListDao.insert(
                PressureListBean(
                    DateUtil.getLongTime(startTime),
                    DateUtil.getLongTime(endTime),
                    name,
                    false,
                    DateUtil.getDateTime(
                        startTime
                    )
                )
            )
        }
    }

    override fun SpecifyTemperatureHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<Int>
    ) {
        val name: String = Gson().toJson(mList)


        val endTmepTime = Hawk.get("last_temp",0L)
        TLog.error("------温度返回=="+endTime+" "+endTmepTime)
        if(endTime == endTmepTime){  //最后一个同步
            MainHomeActivity().setSyncComplete(true)
        }


        getTemp(
            XingLianApplication.TIME_START + startTime,
            XingLianApplication.TIME_START + endTime, mList
        )
        mTempListDao.insert(
            TempListBean(
                XingLianApplication.TIME_START + startTime,
                XingLianApplication.TIME_START + endTime,
                name,
                DateUtil.getDateTime(startTime)
            )
        )
    }

    override fun onResult(
        productNumber: String?,
        versionName: String?,
        version: Int,
        nowMaC: String?,
        mac: String?
    ) {

        var value = HashMap<String, Any>()
        value["mac"] = nowMaC!!.toLowerCase(Locale.CHINA)   //上传到后台转小写
        value["productNumber"] = productNumber!!
        value["firmwareVersion"] = version.toString()
        value["appVersion"] = HelpUtil.getVersionCode(context).toString()
        value["osType"] = "1"
        value["appVersionName"] = HelpUtil.getVersionName(context)!!
        value["productCategoryId"] = XingLianApplication.getXingLianApplication().getDeviceCategoryValue(productNumber).toString()

        TLog.error("--------连接成功设备信息="+Gson().toJson(value))

        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    HomeViewApi.mHomeViewApi.saveUserEquip(value)
                }.onSuccess {
                    TLog.error("----------上传状态-------")
                    SNEventBus.sendEvent(-10,"update_equip")
                }.onFailure {
                    TLog.error("----------上传状态---falie----")
                }
            }
        Hawk.put("address", nowMaC)
        Hawk.put(
            "DeviceFirmwareBean", DeviceFirmwareBean(
                productNumber,
                versionName,
                version,
                nowMaC,
                mac
            )
        )
        SNEventBus.sendEvent(
            DEVICE_FIRMWARE,
            DeviceFirmwareBean(productNumber, versionName, version, nowMaC, mac)
        )

    }

    override fun DeviceMotionResult(mDataBean: DataBean?) {
        Log.e(
            "XLNotify",
            "----333----运动实时数据=" + mDataBean!!.totalSteps + " " + mDataBean!!.distance
        )
        SNEventBus.sendEvent(Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt(), mDataBean)
    }

    override fun DevicePropertiesResult(
        electricity: Int,
        mCurrentBattery: Int,
        mDisplayBattery: Int,
        type: Int
    ) {
        Hawk.put(
            DEVICE_ATTRIBUTE_INFORMATION,
            DevicePropertiesBean(electricity, mCurrentBattery, mDisplayBattery, type)
        )
        SNEventBus.sendEvent(DEVICE_ELECTRICITY, electricity)
    }

    override fun UUIDBindResult(key: Int) {
        if (key == 2) {
            Hawk.put(BIND_UUID, true)
        } else {
            settingInstructions()
            Hawk.put(BIND_UUID, false)
        }
        SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA)
    }

    override fun DoNotDisturbModeSwitchCallResult() {

    }

    override fun TimeCall() {
        TLog.error("天气 设置时间成功")
        SNEventBus.sendEvent(DEVICE_CONNECT_WEATHER_SERVICE)//天气
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            HOME_HISTORICAL_BIG_DATA_WEEK -> {
                bigDataHistoryStatus = event.data as Boolean
                TLog.error("HomeFragment 回调完成了")
                getBigDataHistory()
            }
        }
    }


    fun getHeart(startTime: Long, endTime: Long, heartList: ArrayList<Int>) {
        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    HomeViewApi.mHomeViewApi.setHeartRate(
                        startTime.toString(),
                        endTime.toString(),
                        heartList.toIntArray()
                    )
                }.onSuccess {
                }.onFailure {
                }
            }

    }

    private fun getSleep(bean: SleepBean) {
        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    var value = HashMap<String, Any>()
                    value["startTime"] = bean.startTime + XingLianApplication.TIME_START
                    value["endTime"] = bean.endTime + XingLianApplication.TIME_START
                    value["apneaTime"] = bean.totalApneaTime
                    value["apneaSecond"] = bean.numberOfApnea
                    value["avgHeartRate"] = bean.averageHeartRate
                    value["minHeartRate"] = bean.minimumHeartRate
                    value["maxHeartRate"] = bean.maximumHeartRate
                    value["respiratoryQuality"] = bean.respiratoryQuality
                    value["sleepList"] = Gson().toJson(bean.getmList())
                    HomeViewApi.mHomeViewApi.saveSleep(value)
                }.onSuccess {
                }.onFailure {
                }
            }

    }

    fun getBloodOxygen(startTime: Long, endTime: Long, list: ArrayList<Int>) {
        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    HomeViewApi.mHomeViewApi.saveBloodOxygen(
                        startTime.toString(),
                        endTime.toString(),
                        list.toIntArray()
                    )
                }.onSuccess {
                }.onFailure {
                }
            }

    }

    fun getTemp(startTime: Long, endTime: Long, list: ArrayList<Int>) {

        TLog.error("-------温度温度=="+startTime+" "+endTime)

        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    HomeViewApi.mHomeViewApi.setTemperature(
                        startTime.toString(),
                        endTime.toString(),
                        list.toIntArray()
                    )
                }.onSuccess {
                }.onFailure {
                }
            }
    }

    private fun getPressure(startTime: Long, endTime: Long, list: ArrayList<PressureBean>) {
        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    HomeViewApi.mHomeViewApi.setPressure(
                        startTime.toString(),
                        endTime.toString(),
                        Gson().toJson(list)
                    )
                }.onSuccess {
                }.onFailure {
                }
            }
    }

    private fun getDaily(startTime: Long, endTime: Long, list: String) {
        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    HomeViewApi.mHomeViewApi.saveDailyActive(startTime, endTime, list)
                }.onSuccess {
                }.onFailure {
                }
            }
    }

    var mTimeList: ArrayList<TimeBean> = Hawk.get(TIME_LIST, ArrayList())
    private var scheduleList = Hawk.get<ArrayList<TimeBean>>(SCHEDULE_LIST, ArrayList())
    private var remindTakeList = Hawk.get<ArrayList<RemindTakeMedicineBean>>(REMIND_TAKE_MEDICINE, ArrayList())
    private fun getAllClock() {
        var alarmClockCreateTime = Hawk.get(ALARM_CLOCK_CREATE_TIME, 0L)
        var scheduleCreateTime = Hawk.get(SCHEDULE_CREATE_TIME, 0L)
        var takeMedicineCreateTime = Hawk.get(TAKE_MEDICINE_CREATE_TIME, 0L)
        TLog.error("alarmClockCreateTime+="+alarmClockCreateTime)
        TLog.error("scheduleCreateTime+="+scheduleCreateTime)
        TLog.error("takeMedicineCreateTime+="+takeMedicineCreateTime)
        if (HelpUtil.netWorkCheck(context)) {
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    SetAllClockApi.clockApi.getRemind()
                }.onSuccess {
                    TLog.error("总数据++" + Gson().toJson(it.data))
                    if (it.code == 200) {
                        TLog.error("总数据++" + Gson().toJson(it.data))
                        if (it.data.alarmClock == null ) {
                            saveAlarmClock(System.currentTimeMillis()/1000)
                        } else {
                            var list = it.data.alarmClock.list
                            mTimeList.clear()
                            list.forEach { it ->
                                var bean = TimeBean()
                                bean.characteristic = it.characteristic
                                bean.hours = it.hours
                                bean.mSwitch = it.getmSwitch()
                                bean.min = it.min
                                bean.number = it.number
                                bean.specifiedTime = it.specifiedTime
                                bean.unicode = it.unicode
                                bean.unicodeType = it.unicodeType.toByte()
                                bean.specifiedTimeDescription = it.specifiedTimeDescription
                                bean.endTime = it.endTime
                                mTimeList.add(bean)
                                writeAlarmClockScheduleCall(bean, false)
                            }
                            Hawk.put(TIME_LIST, mTimeList)
                        }
                        if (it.data.schedule == null || scheduleCreateTime > it.data.schedule.createTime) {
                            saveSchedule(scheduleCreateTime)
                        } else {
                            var list = it.data.schedule.list
                            scheduleList.clear()
                            list.forEach { it ->
                                var bean = TimeBean()
                                bean.characteristic = it.characteristic
                                bean.mSwitch = it.getmSwitch()
                                bean.number = it.number
                                bean.unicode = it.unicode
                                bean.unicodeType = it.unicodeType.toByte()
                                bean.endTime = it.endTime
                                bean.year = it.year
                                bean.month = it.month
                                bean.day = it.day
                                bean.hours = it.hours
                                bean.min = it.min
                                scheduleList.add(bean)
                                writeAlarmClockScheduleCall(bean, false)
                            }
                            Hawk.put(TIME_LIST, mTimeList)
                        }
                        if (it.data.takeMedicine == null || takeMedicineCreateTime > it.data.takeMedicine.createTime) {
                            saveTakeMedicine(takeMedicineCreateTime)
                            Hawk.put(TAKE_MEDICINE_CREATE_TIME, it.data.takeMedicine.createTime)
                        } else {
                            remindTakeList= it.data.takeMedicine.list as ArrayList<RemindTakeMedicineBean>?
                            Hawk.put(REMIND_TAKE_MEDICINE,remindTakeList)
                           // Hawk.put(TAKE_MEDICINE_CREATE_TIME, it.data.takeMedicine.createTime)
                        }
                    }
                }.onFailure {
                    TLog.error("IT==" + it.message.toString())
                }
            }
        } else {
            TLog.error("闹钟无网操作")
            if (!mTimeList.isNullOrEmpty()) {
                if (mTimeList.size > 0)
                    for (i in 0 until mTimeList.size)
                        writeAlarmClockScheduleCall(mTimeList[i], false)
            } else {
                var timeBean = TimeBean()
                timeBean.characteristic = 1
                timeBean.number = 0
                timeBean.mSwitch = 0
                timeBean.specifiedTime = 0
                timeBean.hours = 0
                timeBean.min = 0
                writeAlarmClockScheduleCall(timeBean, false)
            }
        }
    }

    private fun saveAlarmClock(deleteTime: Long) {
      //  mTimeList = Hawk.get(TIME_LIST, ArrayList())
        var mAlarmClockList: ArrayList<AlarmClockBean> = ArrayList()
        mTimeList.forEach {
            mAlarmClockList.add(
                AlarmClockBean(
                    it.characteristic,
                    it.hours,
                    it.mSwitch,
                    it.min,
                    it.number,
                    it.specifiedTime,
                    it.unicode,
                    it.dataUnitType,
                    it.getSpecifiedTimeDescription(),
                    it.endTime
                )
            )
        }
        var bean = Gson().toJson(mAlarmClockList)
        var data = HashMap<String, String>()
        data["alarmClock"] = bean
        data["createTime"] = (deleteTime).toString()
        GlobalScope.launch(Dispatchers.IO)
        {
            kotlin.runCatching {
                SetAllClockApi.clockApi.saveAlarmClock(data)
            }.onSuccess {

            }
        }
    }

    /**
     * 修改或保存日程
     */
    private fun saveSchedule(time: Long) {
     //   scheduleList = Hawk.get(SCHEDULE_LIST, ArrayList())
        var mList: ArrayList<AlarmClockBean> = ArrayList()
        scheduleList.forEach {
            mList.add(
                AlarmClockBean(
                    it.characteristic,
                    it.mSwitch,
                    it.number,
                    it.unicode,
                    it.dataUnitType,
                    it.getEndTime(),
                    it.year,
                    it.month,
                    it.day,
                    it.hours,
                    it.min
                )
            )
        }
        var bean = Gson().toJson(mList)
        var data = HashMap<String, String>()
        data["schedule"] = bean
        data["createTime"] = (time).toString()
        GlobalScope.launch(Dispatchers.IO)
        {
            kotlin.runCatching {
                SetAllClockApi.clockApi.saveSchedule(data)
            }.onSuccess {

            }
        }

    }
    private fun saveTakeMedicine(time:Long)
    {
      //  remindTakeList= Hawk.get(REMIND_TAKE_MEDICINE, ArrayList())
        var bean=Gson().toJson(remindTakeList)
        var data= HashMap<String,String>()
        data["takeMedicine"]=bean
        data["createTime"]=(time).toString()
        GlobalScope.launch(Dispatchers.IO)
        {
            kotlin.runCatching {
                SetAllClockApi.clockApi.saveTakeMedicine(data)
            }.onSuccess {

            }
        }
    }

    override fun onResultDialIdBean(bean: MutableList<DialGetAssignCall.DialBean>?) {
        //效验表盘
        if (HelpUtil.netWorkCheck(context))
            GlobalScope.launch(Dispatchers.IO)
            {
                kotlin.runCatching {
                    TLog.error("验证表盘++"+Gson().toJson(bean))
                    RecommendDialViewApi.mRecommendDialViewApi.checkDialSate(Gson().toJson(bean))
                }.onSuccess {
                }.onFailure {
                }
            }
    }

    override fun onLocationChanged(city: String?, latitude: Double, longitude: Double) {
        mLSHelper?.pause()
        if (city == null) return
         val decimalFormat = DecimalFormat("#.##")
        Log.e("定位", "-----城市=$city $longitude $latitude")
        Hawk.put("city", city)
        SNEventBus.sendEvent(LOCATION_INFO, "${decimalFormat.format(longitude)},${decimalFormat.format(latitude)}")

        val intent = Intent()
        intent.action = "com.example.xingliansdk.location"
        intent.putExtra("longitude",longitude)
        intent.putExtra("latitude",latitude)
        context.sendBroadcast(intent)
    }


}