package com.app.fmate

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
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
import androidx.activity.OnBackPressedCallback
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.DeviceFirmwareBean
import com.app.fmate.bean.DevicePropertiesBean
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.broadcast.BluetoothMonitorReceiver
import com.app.fmate.dialog.MeasureBpPromptDialog
import com.app.fmate.dialog.OnCommDialogClickListener
import com.app.fmate.dialog.UpdateDialogView
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.device.DeviceCategoryBean
import com.app.fmate.network.api.otaUpdate.OTAUpdateBean
import com.app.fmate.network.api.weather.ServerWeatherViewModel
import com.app.fmate.network.api.weather.bean.ServerWeatherBean
import com.app.fmate.service.AmapLocationService
import com.app.fmate.service.AppService
import com.app.fmate.service.SNAccessibilityService
import com.app.fmate.ui.BleConnectActivity
import com.app.fmate.ui.bp.MeasureBpBean
import com.app.fmate.ui.bp.MeasureNewBpActivity
import com.app.fmate.utils.*
import com.app.fmate.view.CusDfuAlertDialog
import com.app.fmate.viewmodel.MainViewModel
import com.google.gson.Gson
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_update_zip.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.*


/**
 * 主activity页面
 */
public class MainHomeActivity : BaseActivity<MainViewModel>(),BleWrite.FirmwareInformationInterface ,
    BleWrite.SpecifySleepSourceInterface, MeasureBigBpListener ,AmapLocationService.OnLocationListener{


    var exitTime = 0L
    var bleListener:BluetoothMonitorReceiver?=null

    var devicePropertiesBean : DevicePropertiesBean ?= null

    //手表按按键测量血压的时间，上传后台
    var deviceMeasureTime : String ?= null

    var bleListener1: LocalBroadcastManager? = null
    var bluetoothMonitorReceiver: BluetoothMonitorReceiver? = null
    override fun layoutId() = R.layout.activity_main_home

    public var isBigComplete : Boolean ?=null

    lateinit var otaBean: OTAUpdateBean

    val instance by lazy{this}

    private var otaAlert : AlertDialog.Builder ?= null

    private var cusDufAlert : CusDfuAlertDialog ? = null

    private var measureBpPromptDialog : MeasureBpPromptDialog ?= null



    //定位，获取经纬度，用于天气
    private var amapLocationService : AmapLocationService ?=null


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
        //获取设备类型
        mViewModel.getAllDeviceCategory()

        //获取用户信息
        mainViewModel.userInfo()

        //版本更新
//        mViewModel.appUpdate("aiHealth", HelpUtil.getVersionCode(this))

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

        //设备类型
        mViewModel.deviceCategoryResult.observe(this){
            val     dbBean = it as DeviceCategoryBean
            TLog.error("------获取设备类型列表="+Gson().toJson(dbBean))
            dbBean.list.forEach {
                //类型id
                val categoryId = it.id
                //再遍历每个productNumber
                val producList = it.productList

                producList.forEach {
                    XingLianApplication.getXingLianApplication().setDeviceCategoryKey(it.productNumber,categoryId)
                }
            }
        }





        mViewModel.resultOta.observe(this){
            TLog.error("IT==" + Gson().toJson(it))
            //&& it.versionCode>mDeviceFirmwareBean.version

            if (it.isForceUpdate && it.versionCode>mDeviceFirmwareBean.version) {
                //  showWaitDialog("下载ota升级包中")
                    //判断手表固件是否小于返回固件
                showOtaAlert(it.isForceUpdate,it.platform)
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

        mViewModel.uploadJfBp.observe(this){
            stopMeasure(it as MeasureBpBean)
        }

        mViewModel.msgJfUploadBp.observe(this){

        }

//
//        mainViewModel.resultSleep.observe(this){
//            TLog.error("-------上传睡眠返回="+it.toString())
//            val weatherService = XingLianApplication.getXingLianApplication().getWeatherService()
//            weatherService?.getDevicePPG1CacheRecord()
//        }
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
    private fun showOtaAlert(isFocus : Boolean,platform : String){
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

                if(!TextUtils.isEmpty(platform) && platform == "GR5515"){
                    JumpUtil.startGoodxOTAActivity(instance,adRes.toUpperCase(Locale.CHINA)
                        ,Hawk.get("name")
                        ,mDeviceFirmwareBean.productNumber
                        ,mDeviceFirmwareBean.version
                        ,true
                    )
                }

                if(!TextUtils.isEmpty(platform) && platform=="NORDIC52840"){
                    JumpUtil.startOTAActivity(instance,adRes.toUpperCase(Locale.CHINA)
                        ,Hawk.get("name")
                        ,mDeviceFirmwareBean.productNumber
                        ,mDeviceFirmwareBean.version
                        ,true
                    )
                }

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
        XXPermissions.with(this).permission(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).request { permissions, all ->  }
        //判断是否有保存过Mac
        val isSaveMac = Hawk.get("address","")
        if(!TextUtils.isEmpty(isSaveMac)){
            if (BleConnection.iFonConnectError) {
                TLog.error("没连接的时候重连")

            }
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
        weatherIntentFilter.addAction(Config.WEATHER_START_LOCATION_ACTION)
        weatherIntentFilter.addAction(com.shon.connector.Config.DEVICE_AUTO_MEASURE_BP_ACTION)
        registerReceiver(broadcastReceiver,weatherIntentFilter)
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
                startLocal()
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
                   // mViewModel.getWeatherServer(local)
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
        try {
            unregisterReceiver(bleListener)
            bleListener1?.unregisterReceiver(bluetoothMonitorReceiver!!)
            unregisterReceiver(broadcastReceiver)
            stopLocal()
        }catch (e : Exception){
            e.printStackTrace()
        }

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
            BleWrite.writeCommByteArray(resultByte,true,this)
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
                BleWrite.writeSpecifySleepSourceCall(resultByte,true,startLongTime.toLong(),endLongTime.toLong(),this)
            }
        },1000)
    }

    private val decimalFormat = DecimalFormat("#.##")


    private  val broadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
           val action = intent?.action ?: return

            Log.e("主页定位成功","-----acion="+action)

            if(action == Config.WEATHER_START_LOCATION_ACTION){
                startLocal()
            }

            if(action == "com.example.xingliansdk.location"){
                val longitude = intent.getDoubleExtra("longitude",0.0)
                val latitude = intent.getDoubleExtra("latitude",0.0)
               // mViewModel.getWeatherServer(decimalFormat.format(longitude)+","+decimalFormat.format(latitude))


             //   XingLianApplication.getXingLianApplication().getWeatherService()?.start24HourMethod()
            }


            if(action == com.shon.connector.Config.DEVICE_AUTO_MEASURE_BP_ACTION){

                val isRunn = Utils.isAppRunning(XingLianApplication.mXingLianApplication)


                if(!isRunn)
                    return
                val isForeground = Utils.isForeground(this@MainHomeActivity,MainHomeActivity::class.java.name)

                val typeCode = intent.getIntExtra("bp_status",0)

                TLog.error("-----是否正在运行="+isRunn+" "+typeCode)
                if(typeCode == 5 || typeCode == 4){
                    if(typeCode == 4){
                        com.shon.connector.Config.isNeedTimeOut = true
                        XingLianApplication.mXingLianApplication.getWeatherService()?.backStartMeasureBp(2)
                    }
                    if(!isForeground)
                        return
                    if(measureBpPromptDialog != null && measureBpPromptDialog!!.isShowing){
                        measureBpPromptDialog!!.dismiss()
                    }
                    measureBpPromptDialog = MeasureBpPromptDialog(
                        this@MainHomeActivity,
                        R.style.edit_AlertDialog_style)
                    measureBpPromptDialog!!.show()
                    measureBpPromptDialog!!.isHalfHour=typeCode == 5
                    measureBpPromptDialog!!.setOnCommDialogClickListener(object :
                        OnCommDialogClickListener {
                        override fun onConfirmClick(code: Int) {
                            if(typeCode == 5){
                                startActivity(Intent(
                                    XingLianApplication.mXingLianApplication,
                                    MeasureNewBpActivity::class.java))
                            }

                        }

                        override fun onCancelClick(code: Int) {

                        }

                    })
                }

                if(typeCode == 8 ){
                    measureBpPromptDialog?.dismiss()
                    startActivity(Intent(this@MainHomeActivity,MeasureNewBpActivity::class.java))
                }

                //超时，有弹窗取消弹窗 //有弹窗 取消弹窗
                if(typeCode == 0x01 || typeCode == 10){
                    com.shon.connector.Config.isNeedTimeOut = false
                    measureBpPromptDialog?.dismiss()
                }

                if(typeCode == 0x09){ //后台测量血压
                   // measureBp()
                    com.shon.connector.Config.isNeedTimeOut = true
                    XingLianApplication.mXingLianApplication.getWeatherService()?.backStartMeasureBp(true)
                }



            }

        }

    }




    override fun measureStatus(status: Int,deviceTime : String) {
        if(status == 0x02){
            this.deviceMeasureTime = deviceTime;
        }
        if(status == 0x01){ //手表主动终止

        }
    }

    //测量结果
    override fun measureBpResult(bpValue: MutableList<Int>?, timeStr: String) {
        val stringBuilder = StringBuilder()
        bpValue?.forEachIndexed { index, i ->
            if(index == bpValue.size-1){
                stringBuilder.append(i)
            }else{
                stringBuilder.append(i)
                stringBuilder.append(",")
            }
        }
        if(deviceMeasureTime != null){
            mViewModel.uploadJFBpData(stringBuilder.toString(), deviceMeasureTime!!)
        }

    }

    //上传成功返回测量结果给手表
    private fun stopMeasure(measureBpBean: MeasureBpBean){
        handler.removeMessages(0x00)
        //时间
        val longTime = TimeUtil.formatTimeToLong(deviceMeasureTime,0)
        val timeArray = HexDump.toByteArray(longTime-946656000L)

        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x07,0x02,0x00,0x07,timeArray[0],timeArray[1],timeArray[2],timeArray[3],
            measureBpBean.sbp.toInt().toByte(),measureBpBean.dbp.toInt().toByte(),measureBpBean.heartRate.toInt().toByte()
        )

        val resultArray = CmdUtil.getFullPackage(cmdArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }

        })
    }

    //返回经纬度
    override fun backLocalLatLon(lat: Double, lon: Double,city : String) {
        TLog.error("-----定位获取天气="+lat+" "+city)
         Hawk.put("city",city)
        mViewModel.getWeatherServer(decimalFormat.format(lon)+","+decimalFormat.format(lat))
        stopLocal()
    }


    //开始定位
    private fun startLocal(){
        if(amapLocationService == null){
            amapLocationService = AmapLocationService(this)
        }
        amapLocationService!!.setOnLocationListener(this)
        amapLocationService!!.startLocation()
    }

    private fun stopLocal(){
        if(amapLocationService != null){
            amapLocationService!!.stopLocation()
            //amapLocationService!!.destroyLocation()
        }
    }

    private fun writeAutoBackBp(){
        val cmdArray = byteArrayOf(0x0B, 0x01, 0x01, 0x00, 0x01, 0x02)

        val resultArray = CmdUtil.getFullPackage(cmdArray)

        BleWrite.writeCommByteArray(resultArray, true)
    }
}