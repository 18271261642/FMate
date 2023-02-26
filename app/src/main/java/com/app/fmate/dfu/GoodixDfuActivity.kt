package com.app.fmate.dfu

import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
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
import com.goodix.ble.gr.toolbox.app.libfastdfu.DfuProgressCallback
import com.goodix.ble.gr.toolbox.app.libfastdfu.EasyDfu2
import com.goodix.ble.gr.toolbox.app.libfastdfu.FastDfu
import com.goodix.ble.libcomx.ILogger
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_update_zip.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileInputStream
import java.lang.Error

/**
 * 汇顶的ota
 * Created by Admin
 *Date 2022/6/14
 */
class GoodixDfuActivity : BaseActivity<MyDeviceViewModel>(),View.OnClickListener{


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


    override fun layoutId(): Int {
        return R.layout.activity_update_zip
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        SNEventBus.register(this)

        initData()

        fileName = Hawk.get<String>("OTAFile")
        Hawk.put("dfuAddress", address)

        if (status) {
            tvBegan.visibility = View.VISIBLE
            airUpgradeTv.visibility = View.GONE
            proBar.visibility = View.GONE
        } else {
            tvBegan.visibility = View.GONE
            airUpgradeTv.visibility = View.VISIBLE
            proBar.visibility = View.VISIBLE
        }

        mViewModel.findUpdate(productNumber, version)  //更新下载
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
            TLog.error("IT==" + Gson().toJson(it))
            otaBean = it
            if (otaBean?.ota.isNullOrEmpty())
            {
                tvBegan.visibility=View.GONE
                tvUpdateCode.text = resources.getString(R.string.string_last_version)
            }
            else {
                if(it.versionCode <= mDeviceFirmwareBean.version){
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
                    val batteryManager: BatteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
                    val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    //1正在充电
                    val deviceStatus = Hawk.get("ELECTRICITY_STATUS",0)

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
                var codeName = ByteUtil.hexStringToByte(code)
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



    private fun initData(){
        dfuViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(DFUViewModel::class.java)

        tvNowVersionName.text = mDeviceFirmwareBean.versionName
        address = intent.getStringExtra("address").toString()
        name = intent.getStringExtra("name").toString()
        productNumber = intent.getStringExtra("productNumber").toString()
        status = intent.getBooleanExtra("writeOTAUpdate", false)
        version = intent.getIntExtra("version", 0)


        isOtaInto = intent.getBooleanExtra("is_ota_into",false)

        tvBegan.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvBegan -> {
                status = true
                tvBegan.visibility = View.GONE
                airUpgradeTv.visibility = View.VISIBLE
                proBar.visibility = View.VISIBLE
                otaBean?.let { mViewModel.downLoadZIP(it, downloadCallBac) }

            }
        }
    }


    //开始升级
    private fun startToDfu(){
        try {
            val otaFile = File(fileName)
            TLog.error("------startttt="+fileName+"\n"+(otaFile.isFile))
            if(!otaFile.isFile)
                return
            val connBleDevice: BluetoothDevice = BLEManager.getInstance().connBlueDevice

            val inputStream  = FileInputStream(otaFile)

            TLog.error("--------汇顶升级ble="+connBleDevice.address +" "+(inputStream==null))

            val fastduf = FastDfu()
            val easyDfu2 = EasyDfu2()
            easyDfu2.setLogger(iLog)
            easyDfu2.setListener(dfuCallListenerBack)
            easyDfu2.startDfu(this,connBleDevice,inputStream)
            inputStream.close()

        }catch (e : Exception){
            e.printStackTrace()
        }

    }


    //下载回调
    private var downloadCallBac = object : DownLoadCallback{
        override fun onDownLoadStart(fileName: String?) {
            TLog.error("------开始下载="+fileName)
            this@GoodixDfuActivity.fileName = fileName!!
            ShowToast.showToastLong(resources.getString(R.string.string_ota_down_ing))
            showWaitDialog(resources.getString(R.string.string_ota_down_alert))
        }

        override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
            TLog.error("--------下载onDownLoading="+progress)
        }

        override fun onDownLoadSuccess() {  //下载完成，去升级
            TLog.error("---------下载完成="+isOtaInto)
            hideWaitDialog()
            //是ota模式直接升级
            if(isOtaInto){
               startToDfu()
            }else{
                //下载完成进入ota模式
                    BLEManager.getInstance().dataDispatcher.clear("")
                BleWrite.writeOTAUpdateCall(writeInterface)
                Handler().postDelayed({ // 如果此活动仍处于打开状态并且上传过程已完成，请取消通知
//                TLog.error("5秒")
                    updateMac()
                }, 5000)
            }


        }

        override fun onDownLoadError() {
            TLog.error("--------下载失败=")
        }
    }



    private fun updateMac() {
        var address1 = Hawk.get<String>("address")
        BLEManager.getInstance().disconnectDevice(address1)
        TLog.error("-----开始搜索ota模式下的设备="+address1)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DEVICE_OTA_UPDATE -> {
                TLog.error(
                    "来这里了==升级  mac+" + address
                            + "\n  name==" + name
                            + "\n fileName===" + fileName
                )

                //开始升级
                startToDfu()
            }
        }
    }



    //升级回调
    private var dfuCallListenerBack = object : DfuProgressCallback{
        override fun onDfuStart() {
            TLog.error("----onDfuStart-----")
        }

        override fun onDfuProgress(p0: Int) {
            TLog.error("---onDfuProgress------="+p0)
            proBar.progress = p0
            proBar.isIndeterminate = false
            airUpgradeTv.text = "$p0%"
        }

        override fun onDfuComplete() {
            TLog.error("----onDfuComplete-----")
            airUpgradeTv.setText(R.string.dfu_status_completed)
            proBar.progress = 100
            proBar.isIndeterminate = false

            //升级完成，等待重启，然后需要自己重新连接设备
            Handler().postDelayed({ // 如果此活动仍处于打开状态并且上传过程已完成，请取消通知
                val connBleDevice: BluetoothDevice = BLEManager.getInstance().getBluetoothDevice()
                BLEManager.getInstance().disconnectDevice(connBleDevice.address)
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

        override fun onDfuError(p0: String?, p1: Error?) {
            TLog.error("----onDfuError-----="+p0+" "+p1?.message)
        }

    }

    //dfu log
    private var iLog  = object : ILogger{
        override fun v(p0: String?, p1: String?) {

        }

        override fun d(p0: String?, p1: String?) {

        }

        override fun i(p0: String?, p1: String?) {

        }

        override fun w(p0: String?, p1: String?) {

        }

        override fun e(p0: String?, p1: String?) {

        }

        override fun e(p0: String?, p1: String?, p2: Throwable?) {

        }

    }

    //进入ota模式返回
    private var writeInterface = BleWrite.BleInterface {
        TLog.error("-----进入ota模式了")
        if(Hawk.get("address","").isNotEmpty()) {
            BLEManager.getInstance().disconnectDevice(Hawk.get("address", ""))
            // BLEManager.getInstance().dataDispatcher.clearAll()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        SNEventBus.unregister(this)
    }

}