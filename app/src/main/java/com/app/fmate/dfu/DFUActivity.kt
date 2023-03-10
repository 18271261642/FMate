package com.app.fmate.dfu

import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.app.otalib.DFUViewModel
import com.app.otalib.service.DfuService
import com.app.fmate.Config
import com.app.fmate.MainHomeActivity
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.DevicePropertiesBean
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.otaUpdate.OTAUpdateBean
import com.app.fmate.ui.setting.vewmodel.MyDeviceViewModel
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.InonePowerSaveUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.utils.TLog
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_update_zip.*
import no.nordicsemi.android.dfu.DfuProgressListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * nordic DFU页面
 */
class DFUActivity : BaseActivity<MyDeviceViewModel>(), DfuProgressListener, DownLoadCallback,
    View.OnClickListener, BleWrite.BleInterface {
    private lateinit var dfuViewModel: DFUViewModel
    lateinit var address: String
    lateinit var name: String
    private var fileName: String? = null
    lateinit var productNumber: String
    private var otaBean: OTAUpdateBean? = null
    var status = false
    var version = 0
    var errorNum=0

    //是否是从ota模式扫描进来的
    var isOtaInto = false

    //1在充电
    val isPower =  Hawk.get("ELECTRICITY_STATUS", 0)

    override fun createObserver() {
        mViewModel.result.observe(this) {
            TLog.error("IT==" + Gson().toJson(it))
            otaBean = it
            if (otaBean?.ota.isNullOrEmpty())
            {
                tvBegan.visibility=View.GONE
                tvUpdateCode.text = resources.getString(R.string.string_last_version)
            }
            else {
                if(it.versionCode <= mDeviceFirmwareBean.version ){
                    tvBegan.visibility=View.GONE
                    tvUpdateCode.text = resources.getString(R.string.string_last_version)
                    return@observe
                }
                //ota搜索进入不限制电量限制
                if(!isOtaInto || isPower == 1){

                    val devicePropertiesBean = Hawk.get(
                        Config.database.DEVICE_ATTRIBUTE_INFORMATION,
                        DevicePropertiesBean(0, 0, 0, 0)
                    )

                    val electricity =  Hawk.get<Int>("d_battery",0)
                    var batteryManager: BatteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
                    var  battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    //1正在充电
                    var deviceStatus = Hawk.get("ELECTRICITY_STATUS",0)


                    tvBegan.isClickable = false
                    tvBegan.visibility = View.VISIBLE
                    tvBegan.setBackgroundColor(Color.parseColor("#F1F1F1"))

                    if (deviceStatus != 1) {
                        if (devicePropertiesBean.electricity < 40) {
                            noUpdateTv.visibility = View.VISIBLE
                            ShowToast.showToastLong(resources.getString(R.string.string_ota_40_battery))
                            return@observe
                        }
                    }

                    if (!InonePowerSaveUtil.isCharging(this) && battery < 20) {
                        ShowToast.showToastLong(resources.getString(R.string.string_ota_20_phone))
                        return@observe
                    }
                    if (InonePowerSaveUtil.isCharging(this) && battery < 10) {
                        noUpdateTv.visibility = View.VISIBLE
                        ShowToast.showToastLong(resources.getString(R.string.string_ota_10_phone))
                        return@observe
                    }

                    tvBegan.isClickable = true
                    tvBegan.background = resources.getDrawable(R.drawable.device_repeat_true_green)
                    noUpdateTv.visibility = View.GONE
                }



                //  tvUpdateCode.text = otaBean?.version
                this.version = otaBean?.versionCode!!
                var code = otaBean?.versionCode?.toString(16)
                val codeName = ByteUtil.hexStringToByte(code)
                TLog.error("---------otaCode="+Arrays.toString(codeName)+" "+code)
                val v2 = codeName[2] as Byte
                if (codeName.size >= 3) {
                    name =
                        "V " + codeName[0].toString() + "." + codeName[1].toString() + "." + (if(v2<1) 256+v2 else v2 ).toString()
                }
                tvUpdateCode.text = resources.getString(R.string.string_update_version)+": " + name
                if (BleConnection.startOTAActivity) {
                  //  showWaitDialog("下载ota升级包中")
                   // otaBean?.let { mViewModel.downLoadZIP(it, this) }
                }
            }
        }
        mViewModel.msg.observe(this) {
            TLog.error("不正常")
            ShowToast.showToastLong(it)
            proBar.visibility=View.GONE
            tvBegan.visibility = View.GONE
        }
    }

    //    var mWindow: Window ?=null
    override fun layoutId() = R.layout.activity_update_zip
    override fun initView(savedInstanceState: Bundle?) {
        //  mWindow = window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        SNEventBus.register(this)
        dfuViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

            .create(DFUViewModel::class.java)
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvNowVersionName.text = mDeviceFirmwareBean.versionName
        address = intent.getStringExtra("address").toString()
        name = intent.getStringExtra("name").toString()
        productNumber = intent.getStringExtra("productNumber").toString()
        status = intent.getBooleanExtra("writeOTAUpdate", false)
        version = intent.getIntExtra("version", 0)


        isOtaInto = intent.getBooleanExtra("is_ota_into",false)

        tvBegan.setOnClickListener(this)
        fileName = Hawk.get<String>("OTAFile")
        Hawk.put("dfuAddress", address)
        TLog.error(
            "address=" + address
                    + "    name+=" + name
                    + "    productNumber+=" + productNumber
                    + "    version+=" + version
        )
        TLog.error("fileName==" + fileName)


        if (status) {
            tvBegan.visibility = View.VISIBLE
            airUpgradeTv.visibility = View.GONE
            proBar.visibility = View.GONE
        } else {
            tvBegan.visibility = View.GONE
            airUpgradeTv.visibility = View.VISIBLE
            proBar.visibility = View.VISIBLE
        }
        status = false
        //version = 1  //测试用的
        TLog.error(
            "来了 吧" + productNumber
                    + "===+" + version
        )
        mViewModel.findUpdate(productNumber, version)  //更新下载
//        }
        dfuViewModel.attachView(this, this)
    }

    override fun onDownLoadStart(fileName: String?) {
        this.fileName = fileName!!
        ShowToast.showToastLong(resources.getString(R.string.string_ota_down_ing))
        showWaitDialog(resources.getString(R.string.string_ota_down_alert))
    }

    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {

    }

    override fun onDownLoadSuccess() {
        TLog.error("下载完成")
        hideWaitDialog()
        if (status) //需要时去不需要时另外操作
        {
            TLog.error("status==" + status)

            if(isOtaInto){
                dfuViewModel.startDFU(address, name, "$fileName", this)
                return
            }

            BleWrite.writeOTAUpdateCall(this)
            Handler().postDelayed({ // 如果此活动仍处于打开状态并且上传过程已完成，请取消通知
//                TLog.error("5秒")
                updateMac()
            }, 5000)


        } else {
            dfuViewModel.startDFU(address, name, "$fileName", this)
        }


    }

    override fun onDownLoadError() {
        hideWaitDialog()
        ShowToast.showToastLong("下载错误")
    }

    private fun updateMac() {
        var address1 = Hawk.get<String>("address")
        BLEManager.getInstance().disconnectDevice(address1)
        if(address1.isNotEmpty()&&address1.length>2) {
            val s =
                ByteUtil.byteToHex((address1.substring(address1.length - 2).toInt(16) + 1).toByte())
            address1 = address1.substring(0, address1.length - 2) + s
            TLog.error("再次传入的++$address1")
            address = address1//很奇怪不知道为啥 address会变成以前那个mac地址 现在就address=address1 一次
            Hawk.put("dfuAddress", address1)
            TLog.error("传入ota==true")
            Hawk.put(Config.database.DEVICE_OTA, true)
            Hawk.put("OTAFile", fileName)
            BleConnection.initStart(/*it,*/true)
        }
    }

    override fun onResult() {
        TLog.error("返回了")
        if(Hawk.get("address","").isNotEmpty()) {
            BLEManager.getInstance().disconnectDevice(Hawk.get("address", ""))
           // BLEManager.getInstance().dataDispatcher.clearAll()
        }
        //    updateMac()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvBegan -> {
                status = true
                tvBegan.visibility = View.GONE
                airUpgradeTv.visibility = View.VISIBLE
                proBar.visibility = View.VISIBLE
                otaBean?.let { mViewModel.downLoadZIP(it, this) }

            }
        }
    }

    //**********以下为  DfuProgressListener  的 接口 *********************/
    //********** 升级的时候，设备会重启，所以有设备连接状态的回调   *************/
    override fun onDeviceConnecting(deviceAddress: String?) {
        TLog.error("onDeviceConnecting")
        proBar.isIndeterminate = true
        airUpgradeTv.setText(R.string.dfu_status_connecting)
    }

    override fun onDeviceConnected(deviceAddress: String?) {
        TLog.error("onDeviceConnected")
    }

    override fun onDfuProcessStarting(deviceAddress: String?) {
        TLog.error("onDfuProcessStarting")
    }

    override fun onDfuProcessStarted(deviceAddress: String?) {
        TLog.error("onDfuProcessStarted")
        proBar.isIndeterminate = true
        airUpgradeTv.setText(R.string.dfu_status_starting)
    }

    override fun onEnablingDfuMode(deviceAddress: String?) {
        //打开DFU 模式，开始重启设备，不需要做任何事情
        proBar.isIndeterminate = true
        airUpgradeTv.setText(R.string.dfu_status_switching_to_dfu)
    }

    override fun onProgressChanged(
        deviceAddress: String?,
        percent: Int,
        speed: Float,
        avgSpeed: Float,
        currentPart: Int,
        partsTotal: Int
    ) {
        proBar.progress = percent
        proBar.isIndeterminate = false
        airUpgradeTv.text = "$percent%"
    }

    override fun onFirmwareValidating(deviceAddress: String?) {
        TLog.error("onFirmwareValidating")
        //检测固件
    }

    override fun onDeviceDisconnecting(deviceAddress: String?) {
        TLog.error("onDeviceDisconnecting=="+deviceAddress)
    }

    override fun onDeviceDisconnected(deviceAddress: String?) {
        TLog.error("onDeviceDisconnected")
        proBar.isIndeterminate = true
        airUpgradeTv.setText(R.string.dfu_status_disconnecting)

        Handler().postDelayed({
            TLog.error("5秒")
            updateMac()
        }, 100)
    }

    override fun onDfuCompleted(deviceAddress: String?) {
        TLog.error("onDfuCompleted")
        airUpgradeTv.setText(R.string.dfu_status_completed)
        proBar.progress = 100
        proBar.isIndeterminate = false

        //升级完成，等待重启，然后需要自己重新连接设备
        Handler().postDelayed({ // 如果此活动仍处于打开状态并且上传过程已完成，请取消通知

            if (deviceAddress != null) {
                BLEManager.getInstance().disconnectDevice(deviceAddress)
            }
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(DfuService.NOTIFICATION_ID)
            TLog.error("==" + Hawk.get("address"))
            TLog.error("dfuAddress==" + Hawk.get("dfuAddress"))
            // BLEManager.getInstance().disconnectDevice(Hawk.get("dfuAddress"))
            // BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
            Hawk.put("dfuAddress", "")
//            Hawk.put("address", "")
//            Hawk.put("name", "")
            mDeviceFirmwareBean.version=version
            mDeviceFirmwareBean.versionName=name
            Hawk.put("DeviceFirmwareBean",
                mDeviceFirmwareBean
            )
            BleConnection.iFOta = false
            Hawk.put(Config.database.DEVICE_OTA, false)
//            BleConnection.Unbind = true
         //   BleConnection.initStart()
            SNEventBus.sendEvent(Config.eventBus.DEVICE_OTA_UPDATE_COMPLETE,mDeviceFirmwareBean)
            SNEventBus.sendEvent(Config.eventBus.DEVICE_DELETE_DEVICE)
        }, 200)

        //  JumpUtil.restartApp(this)
         BleConnection.initStart(false) //走重连
        // JumpUtil.startBleConnectActivity(this)
        if(isOtaInto){

            finish()
            AppActivityManager.getInstance()
                .popAllActivityExceptOne(MainHomeActivity::class.java)
            return
        }
        finish()
    }

    override fun onDfuAborted(deviceAddress: String?) {
        TLog.error("onDfuAborted 取消升级 ，等待重启，然后需要自己重新连接设备")
        //取消升级 ，等待重启，然后需要自己重新连接设备
    }

    override fun onError(deviceAddress: String?, error: Int, errorType: Int, message: String?) {
       // ShowToast.showToastLong("升级失败")
        // Hawk.put("ota", true)
        // BleConnection.initStart(true)
        // finish()
        //升级失败   等待重启，然后需要自己重新连接设备
        Hawk.put(Config.database.DEVICE_OTA, true)
        errorNum++
        ShowToast.showToastLong("升级失败,请尝试退出app并重启蓝牙再启动app连接")
        TLog.error(
            "deviceAddress+=${deviceAddress}"
                    + "\nerror+=${error}"
                    + "\nerrorType++${errorType}"
                    + "\nmessage++${message}"
        )
        BLEManager.getInstance().disconnectDevice(Hawk.get("dfuAddress",""))
        BLEManager.getInstance().dataDispatcher.clearAll()
        if(errorNum>3) {
            ShowToast.showToastLong("无法成功升级,请退出app并且重启蓝牙再次尝试")
        finish()
        }
        else
        BleConnection.initStart(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        SNEventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DEVICE_OTA_UPDATE -> {

                TLog.error(
                    "来这里了==升级  mac+" + address
                            + "\n  name==" + name
                            + "\n fileName===" + fileName
                )
                dfuViewModel.startDFU(address, name, "$fileName", this)
            }
        }
    }
}