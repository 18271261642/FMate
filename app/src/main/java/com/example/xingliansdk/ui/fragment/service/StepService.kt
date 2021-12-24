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
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.ui.fragment.map.RunningActivity
import com.shon.connector.utils.TLog
import java.util.*

/**
 * 步数传感器
 */
class StepService : Service(), SensorEventListener {

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

    override fun onCreate() {
        super.onCreate()
        TLog.error("再次进入 onCreate")
        currentStep=0
        initBroadcastReceiver()
        Thread { getStepDetector() }.start()

    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {

        return null
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
        if (countSensor != null) {
            stepSensor = 0
            sensorManager!!.registerListener(
                this@StepService,
                countSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else  if (detectorSensor != null) {
            stepSensor = 1
            sensorManager!!.registerListener(
                this@StepService,
                detectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /**
     * 由传感器记录当前用户运动步数，注意：该传感器只在4.4及以后才有，并且该传感器记录的数据是从设备开机以后不断累加，
     * 只有当用户关机以后，该数据才会清空，所以需要做数据保护
     */
    var step=0
    override fun onSensorChanged(event: SensorEvent) {
        if (stepSensor == 0) {
            val tempStep = event.values[0].toInt()
            TLog.error("tempStep==${tempStep}")
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

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

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
}