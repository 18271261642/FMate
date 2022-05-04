package com.example.xingliansdk

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.DeviceFirmwareBean
import com.example.xingliansdk.bean.DevicePropertiesBean
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.broadcast.BluetoothMonitorReceiver
import com.example.xingliansdk.dfu.DFUActivity
import com.example.xingliansdk.dialog.UpdateDialogView
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.UIUpdate.UIUpdateBean
import com.example.xingliansdk.network.api.otaUpdate.OTAUpdateBean
import com.example.xingliansdk.network.api.weather.ServerWeatherViewModel
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean
import com.example.xingliansdk.network.manager.NetState
import com.example.xingliansdk.service.AppService
import com.example.xingliansdk.service.SNAccessibilityService
import com.example.xingliansdk.service.work.BleWork
import com.example.xingliansdk.ui.BleConnectActivity
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.CusDfuAlertDialog
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.viewmodel.MainViewModel
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.write.deviceclass.DeviceFirmwareCall
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_update_zip.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.util.*
import kotlin.system.exitProcess


public class MainHomeActivity : BaseActivity<MainViewModel>(),BleWrite.FirmwareInformationInterface ,BleWrite.SpecifySleepSourceInterface{
    var exitTime = 0L
    var bleListener:BluetoothMonitorReceiver?=null

    var devicePropertiesBean : DevicePropertiesBean ?= null


    var bleListener1: LocalBroadcastManager? = null
    var bluetoothMonitorReceiver: BluetoothMonitorReceiver? = null
    override fun layoutId() = R.layout.activity_main_home

    public var isBigComplete : Boolean ?=null

    lateinit var otaBean: OTAUpdateBean

    val instance by lazy{this}

    private var otaAlert : AlertDialog.Builder ?= null

    private var cusDufAlert : CusDfuAlertDialog ? = null


    val handler : Handler =  object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0x00){
                val weatherBean = msg.obj;
                analysisWeather(weatherBean as ServerWeatherBean)
            }

            if(msg.what == 88){


            }
        }
    }



    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        Permissions()

        bindBle()
        mainViewModel.userInfo()

        //版本更新
        mViewModel.appUpdate("aiHealth", HelpUtil.getVersionCode(this))

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainHomeActivity, R.id.host_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.mainFragment) {
                    nav.navigateUp()
                } else {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ShowToast.showToastLong("再按一次退出程序")
                        exitTime = System.currentTimeMillis()
                    } else {
                        Hawk.put("iFonConnectError","MainHomeActivity BleConnection.iFonConnectError=true")
                        BleConnection.iFonConnectError=true
                        if(Hawk.get<String>("address").isNotEmpty()) {
                            BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                            BLEManager.getInstance().dataDispatcher.clearAll()
                        }
                        val intent = Intent(this@MainHomeActivity, AppService::class.java)
                        stopService(intent)
                        finish()
                      //  exitProcess(0)
                    }
                }
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultOta.observe(this){
            TLog.error("IT==" + Gson().toJson(it))
            //&& it.versionCode>mDeviceFirmwareBean.version

            if (it.isForceUpdate && it.versionCode>mDeviceFirmwareBean.version) {
                //  showWaitDialog("下载ota升级包中")
                showOtaAlert(it.isForceUpdate)
            }

        }


        mViewModel.serverWeatherData.observe(this){
            Log.e("天气",Gson().toJson(it))

            Log.e("天气bean=","结果="+it.hourly.toString())

            val holidayWeatherList = it

            val message = handler.obtainMessage()
            message.what = 0x00
            message.obj = holidayWeatherList
            handler.sendMessage(message)

        }

      mViewModel.appResult.observe(this){
          TLog.error("--------版本信息="+Gson().toJson(it))
          if(it.ota == null)
              return@observe
          if(it != null && it.ota.isNotEmpty()){

              updateDialog(it.ota,it.isForceUpdate)
          }
      }

    }


    //app版本更新
    private fun updateDialog(upDataUrl : String,forceUpdate: Boolean) {

        val updateDialogView = UpdateDialogView(this,R.style.edit_AlertDialog_style)
        updateDialogView.show()
        updateDialogView.setCancelable(!forceUpdate)
        updateDialogView.setContentTxt("APP有更新是否下载最新app进行升级?")
        updateDialogView.setIsFocus(forceUpdate)
        updateDialogView.setOnUpdateDialogListener(object :
            UpdateDialogView.onUpdateDialogListener {
            override fun onSureClick() {
                if (upDataUrl.isNullOrEmpty()) {
                    ShowToast.showToastLong("地址链接失效")
                    return
                } else {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val contentUrl = Uri.parse(upDataUrl)
                    intent.data = contentUrl
                    startActivity(intent)
                    updateDialogView.dismiss()

                }
            }

            override fun onCancelClick() {
                updateDialogView.dismiss()

            }

        })

    }


    //手表ota更新
    private fun showOtaAlert(isFocus : Boolean){
        var currPower = 100
        if(devicePropertiesBean != null){
            currPower = devicePropertiesBean!!.electricity
        }
        //1在充电
        val isPower =  Hawk.get("ELECTRICITY_STATUS", 0)
        cusDufAlert = CusDfuAlertDialog(instance,R.style.edit_AlertDialog_style)
        cusDufAlert!!.show()
        cusDufAlert!!.setCancelable(false)
        cusDufAlert!!.setNormalShow(if(isPower == 1) false else currPower <40)
        cusDufAlert!!.setOnCusDfuClickListener(object : CusDfuAlertDialog.OnCusDfuClickListener {
            override fun onCancelClick() {
                cusDufAlert!!.dismiss()
            }

            override fun onSUreClick() {
                cusDufAlert!!.dismiss()
                var adRes = Hawk.get("address","")
                JumpUtil.startOTAActivity(instance,adRes.toUpperCase(Locale.CHINA)
                    ,Hawk.get("name")
                    ,mDeviceFirmwareBean.productNumber
                    ,mDeviceFirmwareBean.version
                    ,true
                )
            }

        })

    }




    /**
     * 某些手机会杀掉下面的服务
     */
    private fun restartServiceIfNeed() {

        if (!HelpUtil.isServiceRunning(this, AppService::class.java)) {
            Hawk.put("isServiceStatus","MainHomeActivity BleConnection.isServiceStatus =true")
            BleConnection.isServiceStatus =true
            val intent = Intent(this, AppService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }



    override fun onResume() {
        super.onResume()
//        TLog.error("MainHomeActivity  onResume+"+ BLEManager.isConnected)
        if (BleConnection.iFonConnectError) {
            TLog.error("没连接的时候重连")
            BleConnection.initStart(Hawk.get(Config.database.DEVICE_OTA, false))
        }
        //initPermission2()
        restartServiceIfNeed()
    }

    private var dialog: AlertDialog? = null
    /**
     * 初始化推送服务监听
     */
    private fun initPermission2() {
        //通知监听权限
        //TODO 这里如果重复判断权限==false 可能需要延迟0.5~1秒再判断, 因为系统数据库插入开关值是一个子线程操作, 回到该界面马上调用提供者 有可能获取到的还是之前的开关状态
        val hasNotificationPermission: Boolean =
            PermissionUtils.hasNotificationListenPermission(this)
        val isAccessibilityServiceRunning: Boolean = PermissionUtils.isServiceRunning(
            this,
            SNAccessibilityService::class.java
        )
        //TODO 如果没有通知权限,同时辅助服务没有运行  才提示需要授权,  否则 如果辅助服务在运行,通知服务没运行, 那就先用辅助服务顶替.
        //TODO 注意 这里判断的是两种通知监听服务,勿混淆,  逻辑是 通知服务无效则使用辅助服务,通知服务和辅助服务都有效,则优先使用通知服务作为消息推送主要数据来源
        if (!hasNotificationPermission && !isAccessibilityServiceRunning) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val permissionNames = getString(R.string.content_permission_notification)
            val message =
                SpannableStringBuilder(
                    """
                        ${getString(R.string.content_authorized_to_use)}
                        $permissionNames
                        """.trimIndent()
                )
            message.setSpan(
                ForegroundColorSpan(Color.RED),
                message.length - permissionNames.length,
                message.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            dialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.content_authorized)
                .setMessage(message)
                .setNegativeButton(getString(R.string.content_cancel), null)
                .setPositiveButton(
                    getString(R.string.content_approve)
                ) { dialog, _ -> PermissionUtils.startToNotificationListenSetting(this@MainHomeActivity) }
                .show()
            return
        }
        //请求重新绑定 通知服务,防止未开启
        PermissionUtils.requestRebindNotificationListenerService(this)

        //位置权限,  蓝牙扫描用
        if (!PermissionUtils.hasLocationEnablePermission(this)) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val permissionNames = getString(R.string.content_permission_location)
            val message = SpannableStringBuilder(
                """
              ${getString(R.string.content_authorized_to_use)}
              $permissionNames
              """.trimIndent()
            )
            message.setSpan(
                ForegroundColorSpan(Color.RED),
                message.length - permissionNames.length,
                message.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            dialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.content_authorized)
                .setMessage(message)
                .setNegativeButton(getString(R.string.content_cancel), null)
                .setPositiveButton(getString(R.string.content_approve)
                ) { dialog, which -> PermissionUtils.startToLocationSetting(this@MainHomeActivity) }
                .show()
            return
        }



        if (!PermissionUtils.hasNotificationEnablePermission(this)) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val permissionNames =
                getString(R.string.content_permission_notification_enable)
            val message =
                SpannableStringBuilder(
                    """
                        ${getString(R.string.content_authorized_to_use)}
                        $permissionNames
                        """.trimIndent()
                )
            message.setSpan(
                ForegroundColorSpan(Color.RED),
                message.length - permissionNames.length,
                message.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            dialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.content_authorized)
                .setMessage(message)
                .setNegativeButton(getString(R.string.content_cancel), null)
                .setPositiveButton(
                    getString(R.string.content_approve)
                ) { dialog, which ->
                    PermissionUtils.startToNotificationEnableSetting(
                        this@MainHomeActivity,
                        null
                    )
                }.show()
            return
        }


    }

    private fun bindBle()
    {

        bleListener= BluetoothMonitorReceiver()
        val intentFilter = IntentFilter()
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        // 注册广播
        registerReceiver(bleListener, intentFilter);

        val weatherIntentFilter = IntentFilter()
        weatherIntentFilter.addAction("com.example.xingliansdk.location")
        registerReceiver(broadcastReceiver,weatherIntentFilter)

//        bleListener1 = LocalBroadcastManager.getInstance(this)
//        bluetoothMonitorReceiver = BluetoothMonitorReceiver()
//        val intentFilter1 = IntentFilter()
//        // 监视蓝牙设备与APP连接的状态
//        intentFilter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//        intentFilter1.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
//        // 注册广播
//        bleListener1?.registerReceiver(bluetoothMonitorReceiver!!, intentFilter)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventContent(event: SNEvent<Any>){


        when (event.code){
            Config.eventBus.DEVICE_CONNECT_HOME -> {  //绑定成功
                ShowToast.showToastLong(getString(R.string.bind_success))
                TLog.error("HomeFragment BleConnectActivity==${BleConnectActivity.connect}")
                val bindAddress = Hawk.get<String>("address", "")
                Log.e("主页","------绑定成功后获取的mac="+bindAddress)
//                if(baseDialog.isShowing)
//                    hideWaitDialog()
//                getLastOta()
                //113.887092,22.554868

            }
            Config.eventBus.DEVICE_FIRMWARE->{
                val tmpDeviceBean = event.data as DeviceFirmwareBean
                Log.e("主页","------设备固件信息="+tmpDeviceBean.toString())

                devicePropertiesBean = Hawk.get(
                    Config.database.DEVICE_ATTRIBUTE_INFORMATION,
                    DevicePropertiesBean(0, 0, 0, 0)
                )

                Log.e("主页","-------devicePropertiesBean="+devicePropertiesBean.toString())

                if(tmpDeviceBean.productNumber != null){
                    mDeviceFirmwareBean = tmpDeviceBean
                    getLastOta()
                }
            }


            Config.eventBus.LOCATION_INFO->{
                Log.e("主页","----eventBus---定位成功="+Hawk.get("city"))
                if(event.code == Config.eventBus.LOCATION_INFO){
                    val local: String = event.data as String
                    mViewModel.getWeatherServer(local)
                    //start24HourMethod()
                }
            }

        }
    }

    private fun getLastOta(){
        try {
            BleWrite.writeFlashErasureAssignCall {
                if(mDeviceFirmwareBean.productNumber!=null){
                    var uuid = it
                    TLog.error("主页","uuid.toString()==${uuid.toString()}")
                    TLog.error("主页"," uuid.toString()==${mDeviceFirmwareBean.productNumber}")
                    mViewModel.findUpdate(mDeviceFirmwareBean.productNumber, mDeviceFirmwareBean.version)
                }

                // mViewModel.findUpdate(""+8002,""+251658241)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bleListener)
        bleListener1?.unregisterReceiver(bluetoothMonitorReceiver!!)
        unregisterReceiver(broadcastReceiver)
    }


    //手表连接成功后的固件信息回调
    override fun onResult(
        productNumber: String?,
        versionName: String?,
        version: Int,
        nowMaC: String?,
        mac: String?
    ) {
       Log.e("主页","-------手表连接成功后主页的固件信息回调="+productNumber+" "+versionName+" "+version)
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
        super.onBackPressed()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 过滤按键动作
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true)
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true)
        }
        return super.onKeyDown(keyCode, event)
    }


    private  fun analysisWeather(weatherBean: ServerWeatherBean){

        val weatherService = XingLianApplication.getXingLianApplication().getWeatherService()
        val cityStr = Hawk.get<String>("city")
        Hawk.put("test_weather",Gson().toJson(weatherBean))
        weatherService?.setWeatherData(weatherBean,cityStr)
        val wIntent = Intent();
        wIntent.action = "com.example.xingliansdk.test_weather"
        sendBroadcast(wIntent)
    }

    public fun setSyncComplete(isSync : Boolean){
        handler.postDelayed(Runnable {
            Log.e("三生三世","----isSYnc="+isSync)
            val resultByte = CmdUtil.getFullPackage(byteArrayOf(0x02,0x3D,0x00))
            BleWrite.writeCommByteArray(resultByte,false,this)
        },1000)

    }


    override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {
        if(specifySleepSourceBean != null){
            val constanceMils = 946656000L
            var startTime = specifySleepSourceBean.startTime + constanceMils
            var endTime = specifySleepSourceBean.endTime + constanceMils

            if(startTime < constanceMils)
                startTime = System.currentTimeMillis()/1000
            if(endTime < constanceMils)
                endTime = System.currentTimeMillis()/1000

            ServerWeatherViewModel().postSleepSourceServer(specifySleepSourceBean.remark,startTime,endTime,specifySleepSourceBean.avgActive,specifySleepSourceBean.avgHeartRate)
        }
    }

    override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
        Log.e("睡眠缓存","-----时间戳="+startTime)
        handler.postDelayed(Runnable {
            val btArray = startTime?.get(0)?.let {
                byteArrayOf(0x02,0x3F, it, startTime[1],
                    startTime[2], startTime[3]
                )
            }

            val resultByte = CmdUtil.getFullPackage(btArray)
            val startLongTime = startTime?.get(0)?.let { HexDump.getIntFromBytes(it,startTime[1],startTime[2],startTime[3]) }
            val endLongTime =
                endTime?.get(0)?.let { HexDump.getIntFromBytes(it,endTime[1],endTime[2],endTime[3]) }

            Log.e("睡眠缓存","--222--睡眠-时间戳="+startLongTime+" "+endLongTime)

            if (startLongTime != null && endLongTime != null) {
                BleWrite.writeSpecifySleepSourceCall(resultByte,false,startLongTime.toLong(),endLongTime.toLong(),this)
            }
        },1000)
    }

    private val decimalFormat = DecimalFormat("#.##")


    private  val broadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
           val action = intent?.action ?: return

            Log.e("主页定位成功","-----acion="+action)
            if(action == "com.example.xingliansdk.location"){
                val longitude = intent.getDoubleExtra("longitude",0.0)
                val latitude = intent.getDoubleExtra("latitude",0.0)
                mViewModel.getWeatherServer(decimalFormat.format(longitude)+","+decimalFormat.format(latitude))


             //   XingLianApplication.getXingLianApplication().getWeatherService()?.start24HourMethod()
            }


        }

    }


    //每小时定位一次，发送天气

    //每小时定位一次，发送天气
    /**
     * 通过Handler延迟发送消息的形式实现定时任务。
     */
    private val CHANGE_TIPS_TIMER_INTERVAL =  60 * 1000

    //启动计时器任务
    fun start24HourMethod() {
        val mChangeTipsRunnable: Runnable = object : Runnable {
            override fun run() {
                //开始定位
                BleWork().startLocation(this@MainHomeActivity)
                handler.postDelayed(this, CHANGE_TIPS_TIMER_INTERVAL.toLong())
            }
        }
        handler.post(mChangeTipsRunnable)
    }
}