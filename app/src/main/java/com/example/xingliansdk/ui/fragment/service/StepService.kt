package com.example.xingliansdk.ui.fragment.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import androidx.annotation.Nullable
import com.amap.api.maps.model.LatLng
import com.example.xingliansdk.BaseData
import com.example.xingliansdk.Config
import com.example.xingliansdk.Config.database.SENSOR_STEP_ACTION
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.db.AmapSportBean
import com.example.xingliansdk.bean.db.AmapSportDao
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.javaMapView.MapViewApi
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.service.SendWeatherService
import com.example.xingliansdk.ui.fragment.map.RunningActivity
import com.example.xingliansdk.ui.fragment.map.task.WeakHandler
import com.example.xingliansdk.utils.GPSUtil
import com.example.xingliansdk.utils.ResUtil
import com.example.xingliansdk.utils.Utils
import com.example.xingliansdk.view.DateUtil
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.connector.bean.DeviceInformationBean
import com.shon.connector.utils.TLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

/**
 * 步数传感器
 */
class StepService : Service(), SensorEventListener ,OnSensorStepListener{

    private val tags = "传感器"

    //保留两位小数
    private val decimal = DecimalFormat("#.##")

    //当前步数
    private var currentStep: Int = 0
    //传感器
    private var sensorManager: SensorManager? = null

    //计步传感器类型 0-counter 1-detector
    private var stepSensor = -1
    //广播接收
    private var mInfoReceiver: BroadcastReceiver? = null

    private var builder: Notification.Builder? = null

    private var notificationManager: NotificationManager? = null
    private var nfIntent: Intent? = null
    //下次记录之前的步数
    private var previousStepCount: Int = 0
    //是否有当天的记录
    private var hasRecord: Boolean = false
    //未记录之前的步数
    private var hasStepCount: Int = 0

    private val stepBinder = StepBinder()

    //保存在本地的用户信息bean
    var mDeviceInformationBean : DeviceInformationBean ?= null

    //是否开始运动
    private var isStartSport = false
    //是否暂停运动
    private var isPauseSport = false

    //传感器运动的心率集合，可为空
    private  var heartList: ArrayList<Int> = arrayListOf()
    //时间，从运动页面传递过来
    private var countSportTime : String ?= null


    //用于最后上传bean
    private var amapSportBean : AmapSportBean ? = null



    private var isUnit = true

    override fun onCreate() {
        super.onCreate()
        TLog.error(tags,"再次进入 onCreate")
        currentStep=0
        initBroadcastReceiver()
        Thread { getStepDetector() }.start()
        var year = Calendar.getInstance()
        year.roll(Calendar.YEAR, -18)
        year.timeInMillis
        var birth: Long = year.timeInMillis
        mDeviceInformationBean = Hawk.get(
            Config.database.PERSONAL_INFORMATION,
            DeviceInformationBean(2, 0, 160, 50f, 0, 0, 0, 0, 0, 0, 10000, birth)
        )

        isUnit = mDeviceInformationBean?.unitSystem?.toInt() == 2
        amapSportBean = AmapSportBean()

    }

    @Nullable
    override fun onBind(intent: Intent): IBinder {

        return stepBinder
    }


    public fun setStopParams(htList : MutableList<Int>,sportTime : String){
        this.heartList = htList as ArrayList<Int>
        this.countSportTime = sportTime
    }

    inner class StepBinder : Binder() {
        val stepService: StepService
            get() = this@StepService

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        /**
         * 此处设将Service为前台，不然当APP结束以后很容易被GC给干掉，
         * 这也就是大多数音乐播放器会在状态栏设置一个原理大都是相通的
         */
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        //----------------  针对8.0 新增代码 --------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = Notification.Builder(this.applicationContext, ConstantData.CHANNEL_ID)
            val notificationChannel =
                NotificationChannel(
                    ConstantData.CHANNEL_ID,
                    ConstantData.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_MIN
                )
            notificationChannel.enableLights(false)//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false)//是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            notificationManager?.createNotificationChannel(notificationChannel)
            builder?.setChannelId(ConstantData.CHANNEL_ID)
        } else {
            builder = Notification.Builder(this.applicationContext)
        }

        /**
         * 设置点击通知栏打开的界面，此处需要注意了，
         * 如果你的计步界面不在主界面，则需要判断app是否已经启动，
         * 再来确定跳转页面，这里面太多坑
         */
        nfIntent = Intent(this, RunningActivity::class.java)
        setStepBuilder()
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(ConstantData.NOTIFY_ID, builder?.build())// 开始前台服务
        return START_STICKY
    }



    /**
     * 初始化广播
     */
    private fun initBroadcastReceiver() {
        val filter = IntentFilter()
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN)
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT)
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        //监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED)
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        filter.addAction(Intent.ACTION_TIME_TICK)

        mInfoReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    // 屏幕灭屏广播
                    Intent.ACTION_SCREEN_OFF -> saveStepData()
                    //关机广播，保存好当前数据
                    Intent.ACTION_SHUTDOWN -> saveStepData()
                    // 屏幕解锁广播
                    Intent.ACTION_USER_PRESENT -> saveStepData()
                    // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
                    // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
                    // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
                    Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> saveStepData()
                    //监听日期变化
                    Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIME_TICK -> {
                        saveStepData()

                    }
                }
            }
        }
        //注册广播
        registerReceiver(mInfoReceiver, filter)
    }




    /**
     * 获取传感器实例
     */
    private fun getStepDetector() {
        if (sensorManager != null) {
            sensorManager = null
        }
        // 获取传感器管理器的实例
        sensorManager = this
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //android4.4以后可以使用计步传感器
        if (Build.VERSION.SDK_INT >= 19) {
            addCountStepListener()
        }
    }

    /**
     * 添加传感器监听
     */
    private fun addCountStepListener() {
        //为什么取消第一个 讲一下因为第一个是步数累计会自动累计下去然而我们的步数是每次进去是从新累计所以不需要
        val countSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val detectorSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        val stepSensors = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager!!.registerListener(this,stepSensors,SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager!!.registerListener(this,detectorSensor,SensorManager.SENSOR_DELAY_FASTEST)

//        if (countSensor != null) {
//            stepSensor = 0
//            sensorManager!!.registerListener(
//                this@StepService,
//                countSensor,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//        } else  if (detectorSensor != null) {
//            stepSensor = 1
//            sensorManager!!.registerListener(
//                this@StepService,
//                detectorSensor,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//        }
    }


    //开始运动
    public fun startToSensorSport(){
        countSTep = 0
        isStartSport = true

    }


    //暂停运动
    public fun pauseToSensorSport(isPause : Boolean){
        isPauseSport = isPause
    }

    //结束运动
    public fun stopToSensorSport(){
        isStartSport = false
        saveSensorSport()
    }


    /**
     *  amapSportBean.setDayDate(Utils.getCurrentDate());
    amapSportBean.setYearMonth(Utils.getCurrentDateByFormat("yyyy-MM"));
    amapSportBean.setSportType(sportType);
    amapSportBean.setMapType(1);
    amapSportBean.setCurrentSportTime(millisecondStr);
    amapSportBean.setEndSportTime(Utils.getCurrentDate1());
    amapSportBean.setCurrentSteps(step);
    amapSportBean.setDistance(countDistance);
    amapSportBean.setCalories(countCalories);
    amapSportBean.setAverageSpeed(""+avgSpeed);
    TLog.Companion.error("paceStr=="+paceStr);
    amapSportBean.setPace(paceStr);
    amapSportBean.setLatLonArrayStr(latStr);
    amapSportBean.setCreateTime(createTime/1000);
    TLog.Companion.error("心率==="+new Gson().toJson(heartList));
    amapSportBean.setHeartArrayStr(new Gson().toJson(heartList));
    TLog.Companion.error("-----保存cans="+new Gson().toJson(amapSportBean));
     */
    private fun saveSensorSport(){
        if(amapSportBean == null)
            return
        //GPS打开不保存
        if(GPSUtil.isGpsEnable(this))
            return
        val loginBean =
            Hawk.get<LoginBean>(Config.database.USER_INFO) ?: return
        //mac地址

        //mac地址
        val macAddress = Hawk.get<String>("address")

        amapSportBean?.userId = loginBean.user.userId
        amapSportBean?.deviceMac = macAddress
        amapSportBean?.dayDate = Utils.getCurrentDate()
        amapSportBean?.yearMonth = Utils.getCurrentDateByFormat("yyyy-MM")

        val sType = Hawk.get(Config.database.AMAP_SPORT_TYPE,1)

        amapSportBean?.sportType = sType
        amapSportBean?.mapType = 1
        amapSportBean?.endSportTime = Utils.getCurrentDate1()

        amapSportBean?.latLonArrayStr = null
        amapSportBean?.createTime = System.currentTimeMillis() / 1000

        amapSportBean?.currentSportTime = "00:$countSportTime"
        amapSportBean?.heartArrayStr =  Gson().toJson(heartList)


        //平均速度 米/秒
        //  String avgSpeed = baseSportData.speedAvg+"";
        //平均速度 米/秒
        //  String avgSpeed = baseSportData.speedAvg+"";
        var disC = amapSportBean?.distance?.toDouble()
        //时长
        val countTimeL = getAnalysisTime()

        if(disC == null)
            disC = 0.0

        val avgSpeed: Double = Utils.divi(disC.toDouble() ,countTimeL.toDouble(),2)
        amapSportBean?.averageSpeed = Utils.mul(avgSpeed,3.6).toString()

        val paceStr = disC?.let { Utils.divi(countTimeL.toDouble(), it.toDouble(),2) }
        amapSportBean?.pace = paceStr.toString()

       // amapSportBean?.distance = Utils.mul(disC,1000.0).toString()


        TLog.error(tags,"-------结束运动="+amapSportBean.toString())

        val latLng = LatLng(39.91,116.39)
        val latList = arrayListOf(latLng)

        val hashMap = HashMap<String, Any>()
        hashMap["positionData"] = Gson().toJson(latList)
        hashMap["createTimeStamp"] =System.currentTimeMillis() / 1000
        hashMap["type"] = 1
        hashMap["distance"] = amapSportBean?.distance.toString()
        hashMap["motionTime"] = getAnalysisTime()
        hashMap["calorie"] = amapSportBean?.calories.toString()
        hashMap["steps"] = countSTep
        hashMap["avgPace"] = paceStr!!
        hashMap["avgSpeed"] = avgSpeed
        hashMap["heartRateData"] = Gson().toJson(heartList)


        val bean = MapViewApi.mapViewApi.motionInfoSave(hashMap)
        bean.enqueue(object : Callback<BaseData>{
            override fun onResponse(call: Call<BaseData>, response: Response<BaseData>) {
               TLog.error(tags,"------上传数据="+response.message())

                val mAmapSportDao = instance.getAmapSportDao()
                mAmapSportDao.insert(amapSportBean!!)
            }

            override fun onFailure(call: Call<BaseData>, t: Throwable) {

            }

        })

    }

    /**
     * 由传感器记录当前用户运动步数，注意：该传感器只在4.4及以后才有，并且该传感器记录的数据是从设备开机以后不断累加，
     * 只有当用户关机以后，该数据才会清空，所以需要做数据保护
     */
    var step=0

    var countSTep = 0

    override fun onSensorChanged(event: SensorEvent) {
        TLog.error(tags,"--------类型="+event.sensor.type)
        if(!isStartSport)
            return

        if(isPauseSport)
            return

        if(event.sensor.type == Sensor.TYPE_STEP_COUNTER){
            val sensorSteps = event.values[0].toInt()
            val currTimeLong = event.timestamp
            TLog.error(tags,"tempStep==${sensorSteps}"+" 对应时间="+currTimeLong+" "+DateUtil.getDate("yyyy-MM-dd HH:mm:ss",currTimeLong))
        }


        if(event.sensor.type == Sensor.TYPE_STEP_DETECTOR){
            val tmpS = event.values[0].toInt()
            TLog.error(tags,"--------实时="+event.values[0])
            if(tmpS == 1){
                countSTep++
                //体重
                var userWeight = mDeviceInformationBean?.weight
                if(userWeight == null)
                    userWeight = 60f
                else
                    userWeight

                //身高
                var userHeight = mDeviceInformationBean?.height
                if(userHeight == null)
                    userHeight = 170

                //计算距离
                val currDistance = Utils.divi((countSTep * userHeight * 0.46 ),1000 * 100.0,2)
                //卡路里
                //跑步热量（kcal）＝体重（kg）×距离（公里）×K（运动系数

                /**
                 * 健走 K =0.8214
                跑步 K =1.036
                自行车 K =0.6142
                轮滑、溜冰 K =0.518
                室外滑雪 K =0.888
                 */

                val currKcal = userWeight * currDistance * 1.036f

                //当前步数
                amapSportBean?.currentSteps = countSTep;

                //距离 米
                amapSportBean?.distance   = decimal.format(currDistance * 1000).toString()
                //卡路里
                amapSportBean?.calories = decimal.format(currKcal).toString()

                TLog.error(tags,"--------计算数据="+currDistance +" "+currKcal +" 步数="+countSTep)

                val showDis =
                    ResUtil.format(
                        "%.2f",
                        if (isUnit) Utils.kmToMile(currDistance.toDouble()) else currDistance
                    )
                setSensorBroadCast(showDis,decimal.format(currKcal).toString())


               // sensorImpl.onSensorUpdateSportData(amapSportBean?.distance,amapSportBean?.calories,"0","0",null)


              //  onSensorUpdateSportData(amapSportBean?.distance,amapSportBean?.calories,"0","0",null)
            }
        }

        if (stepSensor == 0) {
            val tempStep = event.values[0].toInt()
            //TLog.error("tempStep==${tempStep}")
            if (!hasRecord) {
                hasRecord = true
                hasStepCount = tempStep
                Log.e( "onSensorChanged: "," !hasRecord  hasStepCount==="+hasStepCount )
            } else {
                Log.e("onSensorChanged: ", "stepSensor==0 tempStep==${tempStep}")
                val thisStepCount = tempStep - hasStepCount
                currentStep += thisStepCount - previousStepCount
                previousStepCount = thisStepCount
                Log.e(
                    "onSensorChanged: ",
                    "previousStepCount==${tempStep}  currentStep==${currentStep}==+${previousStepCount}"
                )
            }
            SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_STEP,currentStep)
            saveStepData()
        } else if (stepSensor == 1) {
            if (event.values[0].toDouble() == 1.0) {
                currentStep++
             //   Log.e( "onSensorChanged: ","stepSensor==0 currentStep==${currentStep}" )
                TLog.error("stepSensor==0 currentStep==${currentStep}")

                saveStepData()
            }
        }
    }


    private fun setSensorBroadCast(dis : String,kcal : String){
        val intent = Intent();
        intent.setAction(SENSOR_STEP_ACTION)
        intent.putExtra("sensor_dis",dis)
        intent.putExtra("sensor_cal",kcal)
        sendBroadcast(intent)
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.e("传感器","------sensor="+accuracy)
    }


    /**
     * 保存当天的数据到数据库中，并去刷新通知栏
     */
    private fun saveStepData() {

        setStepBuilder()
    }

    private fun setStepBuilder() {
        builder?.setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                nfIntent,
                0
            )
        ) // 设置PendingIntent
            ?.setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.mipmap.ic_launcher
                )
            )
            ?.setContentTitle("今日步数" + currentStep + "步")
            ?.setSmallIcon(R.mipmap.ic_launcher)
            ?.setContentText("加油，要记得勤加运动哦")
        // 获取构建好的Notification
        val stepNotification = builder?.build()
        //调用更新
        notificationManager?.notify(ConstantData.NOTIFY_ID, stepNotification)
    }

    override fun onDestroy() {
        super.onDestroy()
        //主界面中需要手动调用stop方法service才会结束
        stopForeground(true)
        unregisterReceiver(mInfoReceiver)

    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }



    //解析总运动时间，结束运动后传递过来的时间为HH:mm:ss格式，转换成秒
    public fun getAnalysisTime(): Int {
        if(countSportTime == null)
            return 0
        var timeArray = countSportTime!!.split(":")

        val m = timeArray[0].toInt()
        val s = timeArray[1].toInt()

        return  m * 60 + s;

    }

    override fun onSensorUpdateSportData(
        distances: String?,
        calories: String?,
        hourSpeed: String?,
        pace: String?,
        latLngs: List<LatLng?>?
    ) {
        TODO("Not yet implemented")
    }
}