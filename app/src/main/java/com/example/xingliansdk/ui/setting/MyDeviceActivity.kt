package com.example.xingliansdk.ui.setting

import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import com.example.xingliansdk.Config
import com.example.xingliansdk.Config.database.DEVICE_OTA
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.DeviceFirmwareBean
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
//import com.example.xingliansdk.pictureselector.GlideEngine
import com.example.xingliansdk.ui.setting.MyDeviceActivity.FlashBean.UIFlash
import com.example.xingliansdk.ui.setting.vewmodel.MyDeviceViewModel
import com.example.xingliansdk.utils.*
import com.github.iielse.switchbutton.SwitchView
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
//import com.luck.picture.lib.PictureSelector
//import com.luck.picture.lib.config.PictureConfig
//import com.luck.picture.lib.config.PictureMimeType
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.DataDispatcher
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.bean.RemindTakeMedicineBean
import com.shon.connector.bean.TimeBean
import com.shon.connector.utils.TLog
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_my_device.*
import kotlinx.android.synthetic.main.activity_my_device.titleBar
import kotlinx.android.synthetic.main.item_switch.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

//手表设置页面
class MyDeviceActivity : BaseActivity<MyDeviceViewModel>(), View.OnClickListener {
    object FlashBean
    { var  UIFlash=true }
    var electricity=0
    override fun layoutId() = R.layout.activity_my_device
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        SNEventBus.register(this)
        electricity=intent.getIntExtra("electricity",0)
        initBind()
        getStatus()
        initOnclick()
        settingCleanRoom.setContentText(AppUtils.getTotalCacheSize(this))
        settingUpdate.setContentText(mDeviceFirmwareBean.versionName)
    }
    override  fun createObserver(){
        mViewModel.resultUserInfo.observe(this)
        {
            TLog.error("=="+Gson().toJson(it))
//            userInfo=it
            userInfo.user = it.user
            userInfo.userConfig = it.userConfig
           // userInfo.permission = it.permission
            Hawk.put(Config.database.USER_INFO, userInfo)
        }
   }

    private fun initOnclick() {
        settingInformationReminder.setOnClickListener(this)
        settingAlarmClock.setOnClickListener(this)
        settingTakeMedicine.setOnClickListener(this)
        settingScheduleReminder.setOnClickListener(this)
        settingWear.setOnClickListener(this)
        settingBloodPressure.setOnClickListener(this)
        settingBrightScreenTime.setOnClickListener(this)
        settingSwitch.setOnClickListener(this)
        settingBrightness.setOnClickListener(this)
        settingCamera.setOnClickListener(this)
        settingWatch.setOnClickListener(this)
        settingUpdate.setOnClickListener(this)
        settingCleanRoom.setOnClickListener(this)
        settingReset.setOnClickListener(this)
        tvDeviceDelete.setOnClickListener(this)
        settingFindDevice.setOnClickListener(this)
        settingWuRao.setOnClickListener(this)
        settingUIUpdate.setOnClickListener(this)
        settingHeartRateAlarmSwitch.setOnClickListener(this)

    }

    private fun getStatus() {
        if(!userInfo?.permission?.flashUpdate.isNullOrEmpty())
        if(userInfo.permission.flashUpdate.toInt()==2)
            settingUIUpdate.visibility=View.VISIBLE
        else
            settingUIUpdate.visibility=View.GONE
        val mTurnOnScreen =userInfo.userConfig.turnScreen.toInt()
        //亮屏
            includeRotateBrightScreen.Switch.isOpened = mTurnOnScreen == 2
        //久坐
        val sedentary =userInfo.userConfig.sedentary.toInt() // Hawk.get<TimeBean>(Config.database.REMINDER_SEDENTARY)
        if (sedentary != null) {
            includeSedentaryReminder.Switch.isOpened = sedentary == 2
        }
        //喝水
        val mDrinkWater = userInfo.userConfig.drinkWater.toInt() // Hawk.get<TimeBean>(Config.database.REMINDER_DRINK_WATER)
        if (mDrinkWater != null) {
            includeDrinkWaterReminder.Switch.isOpened = mDrinkWater == 2
        }
    }

    private fun sedentary(status: Int = 1) {
        userInfo.userConfig.sedentary=status.toString()
        val timeBean = TimeBean()
        timeBean.switch = status
        timeBean.specifiedTime = 127
        timeBean.openHour = 8
        timeBean.openMin = 0
        timeBean.closeHour = 22
        timeBean.closeMin = 0
        timeBean.reminderInterval = 60
        Hawk.put(Config.database.REMINDER_SEDENTARY, timeBean)
        BleWrite.writeReminderSedentaryCall(timeBean)
        includeSedentaryReminder.Switch.isOpened = status == 2
        setSwitchButton()
    }

    private fun drinkWater(status: Int = 1) {
        TLog.error("喝水$status")
        userInfo.userConfig.drinkWater=status.toString()
        val timeBean = TimeBean()
        timeBean.switch = status
        timeBean.specifiedTime = 127
        timeBean.openHour = 8
        timeBean.openMin = 0
        timeBean.closeHour = 22
        timeBean.closeMin = 0
        timeBean.reminderInterval = 60
        Hawk.put(Config.database.REMINDER_DRINK_WATER, timeBean)
        BleWrite.writeReminderDrinkWaterCall(timeBean)
        includeDrinkWaterReminder.Switch.isOpened = status == 2
        setSwitchButton()
    }

    private fun initBind() {
        includeSedentaryReminder.tv_name.text = "久坐提醒"
        includeSedentaryReminder.img.setImageResource(R.mipmap.icon_device_sedentary_reminder)
        includeSedentaryReminder.tv_sub.visibility=View.VISIBLE//8-11号又加回来
        includeSedentaryReminder.tv_sub.text = "久坐1小时提醒"

        includeDrinkWaterReminder.tv_name.text = "喝水提醒"
        includeDrinkWaterReminder.img.setImageResource(R.mipmap.icon_device_drink)
        includeDrinkWaterReminder.tv_sub.text = "喝水1小时提醒"

//        includeHeartRateAlarmSwitch.tv_name.text = "心率报警"
//        includeHeartRateAlarmSwitch.img.setImageResource(R.mipmap.icon_device_heart_rate_alarm)
        includeRotateBrightScreen.tv_name.text = "转腕亮屏"
        includeRotateBrightScreen.img.setImageResource(R.mipmap.icon_device_zhuanwan)
        includeIncomingCall.tv_name.text = "来电提醒"
        includeIncomingCall.img.setImageResource(R.mipmap.icon_device_phone_remind)
//        includeBindDevice.tv_name.text = "绑定设备"
//        includeBindDevice.img.setImageResource(R.mipmap.icon_device_bind)
        includeLowPowerMode.tv_name.text = "低功耗模式"
        includeLowPowerMode.img.setImageResource(R.mipmap.icon_device_low_power_consumption)
        includeSedentaryReminder.Switch.setOnStateChangedListener(object :
            SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                sedentary(2)
            }

            override fun toggleToOff(view: SwitchView?) {
                sedentary(1)
            }
        })
        includeDrinkWaterReminder.Switch.setOnStateChangedListener(object :
            SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                TLog.error("toggleToOn")
                drinkWater(2)
            }

            override fun toggleToOff(view: SwitchView?) {
                TLog.error("toggleToOff")
                drinkWater(1)
            }
        })

        includeRotateBrightScreen.Switch.setOnStateChangedListener(object :
            SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                userInfo.userConfig.turnScreen="2"
                setSwitchButton()
                BleWrite.writeTurnOnScreenCall(2)
                includeRotateBrightScreen.Switch.isOpened = true
            }

            override fun toggleToOff(view: SwitchView?) {
                userInfo.userConfig.turnScreen="1"
                setSwitchButton()
                BleWrite.writeTurnOnScreenCall(1)
                includeRotateBrightScreen.Switch.isOpened = false
            }
        })
        includeIncomingCall.Switch.setOnStateChangedListener(object :
            SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                includeIncomingCall.Switch.isOpened = true
            }

            override fun toggleToOff(view: SwitchView?) {
                includeIncomingCall.Switch.isOpened = false
            }
        })

        includeLowPowerMode.Switch.setOnStateChangedListener(object :
            SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                includeLowPowerMode.Switch.isOpened = true
            }

            override fun toggleToOff(view: SwitchView?) {
                includeLowPowerMode.Switch.isOpened = false
            }
        })
    }

    private fun setSwitchButton() {
        var value = HashMap<String, String>()
        value["turnScreen"] =userInfo.userConfig.turnScreen
        value["drinkWater"]=userInfo.userConfig.drinkWater
        value["sedentary"]=userInfo.userConfig.sedentary
        mViewModel.setUserInfo(value)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingInformationReminder -> {
                JumpUtil.startInfRemindActivity(this)
            }
            R.id.settingAlarmClock -> {
                JumpUtil.startAlarmClockListActivity(this)
            }
            R.id.settingTakeMedicine -> {   //吃药提醒
                JumpUtil.startTakeMedicineIndexActivity(this)
            }
            R.id.settingScheduleReminder -> {
                JumpUtil.startScheduleListActivity(this)
            }
            R.id.settingWear -> {
                wearDialog()
            }
            R.id.settingBloodPressure -> {
            }
            R.id.settingBrightScreenTime -> {
            }
            R.id.settingSwitch -> {
            }
            R.id.settingBrightness -> {
            }
            R.id.settingCamera -> {
                BleWrite.writeCameraSwitchCall(2)
                JumpUtil.startCameraActivity(this)
            }
            R.id.settingWatch -> {
            }
            R.id.settingUpdate -> {
             //   mViewModel.findUpdate(mDeviceFirmwareBean.productNumber,mDeviceFirmwareBean.version)
                var batteryManager:BatteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
                var  battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                TLog.error("电量++"+battery)
                if(!HelpUtil.netWorkCheck(this))
                {
                    ShowToast.showToastLong("网络出问题了，快去检查一下吧～")
                    return
                }
                var status = Hawk.get("ELECTRICITY_STATUS",0)
                if(electricity<40&&status<=0) //40电量 小于说的  2021 -11-17 19.08
                {
                        ShowToast.showToastLong("手表电量低于40%,请充电")
                        return
                }
                else
                {
                    if (!InonePowerSaveUtil.isCharging(this)&& battery<20)
                    {
                        ShowToast.showToastLong("手机电量低于20%,请充电")
                        return
                    }
                    else if(InonePowerSaveUtil.isCharging(this)&& battery<10)
                    {
                        ShowToast.showToastLong("手机电量低于10%,请充电达到10%再进行升级")
                        return
                    }
                }

                BleConnection.startOTAActivity=false//不需要在ota的时候再次跳转
                JumpUtil.startOTAActivity(this,Hawk.get("address")
                    ,Hawk.get("name")
                    ,mDeviceFirmwareBean.productNumber
                    ,mDeviceFirmwareBean.version
                    ,true
                )
//                AllGenJIDialog.updateDialog(supportFragmentManager,this
//                        ,mDeviceFirmwareBean.productNumber,mDeviceFirmwareBean.version)
            }
            R.id.settingUIUpdate->{
                if(!DataDispatcher.callDequeStatus)
                {
                    ShowToast.showToastLong("正在同步其他数据,请同步完成再进行升级")
                    return
                }
                JumpUtil.startFlashActivity(this)
                UIFlash=false
            }
            R.id.settingCleanRoom -> {
                wearDialog(R.id.settingCleanRoom)
            }
            R.id.settingReset -> {
                wearDialog(R.id.settingReset)
            }
            R.id.tvDeviceDelete -> {
                wearDialog(R.id.tvDeviceDelete)
            }
            R.id.settingFindDevice -> {
                AllGenJIDialog.findDialog(supportFragmentManager)
                BleWrite.writeFindDeviceSwitchCall(2)
            }
            R.id.settingWuRao->
            {
              //  JumpUtil.startDoNotDisturbActivity(this)
            }
            R.id.settingHeartRateAlarmSwitch->
            {
                JumpUtil.startHeartRateAlarmActivity(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }

    private fun wearDialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_wear
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_BOTTOM
            animStyle = R.style.BottomTransAlphaADAnimation
            var mList: MutableList<RemindTakeMedicineBean.ReminderGroup> = arrayListOf()
            convertListenerFun { holder, dialog ->
                var tvDele = holder.getView<TextView>(R.id.tvDele)
                var tvOne = holder.getView<TextView>(R.id.tvOne)
                var tvTwo = holder.getView<TextView>(R.id.tvTwo)

                tvOne?.setOnClickListener {
                    settingWear.setContentText("左手")
                    dialog.dismiss()
                }
                tvTwo?.setOnClickListener {
                    settingWear.setContentText("右手")
                    dialog.dismiss()
                }
                tvDele?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    private fun wearDialog(id: Int) {
        newGenjiDialog {
            layoutId = R.layout.alert_dialog_login
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_CENTER
            animStyle = R.style.BottomTransAlphaADAnimation
            convertListenerFun { holder, dialog ->
                var btnOk = holder.getView<TextView>(R.id.dialog_confirm)
                var btnCancel = holder.getView<TextView>(R.id.dialog_cancel)
                var tvTitle = holder.getView<TextView>(R.id.tv_title)
                var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                tvTitle?.text = "提示"
                when (id) {
                    R.id.settingReset -> dialogContent?.text =
                        resources.getString(R.string.content_want_reset)
                    R.id.tvDeviceDelete -> {
                        dialogContent?.text = resources.getString(R.string.content_delete_device)
                    }
                    R.id.settingCleanRoom -> {
                        dialogContent?.text = resources.getString(R.string.content_clean_cache)
                    }
                    else -> dialogContent?.text =
                        resources.getString(R.string.content_want_disconnect)
                }
                btnOk?.setOnClickListener {
                    when (id) {
                        R.id.settingReset -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                AppDataBase.instance.clearAllTables()
                            //    JumpUtil.startBleConnectActivity(this@MyDeviceActivity)
                                BleWrite.writeFactoryRestorationResetCall()
                                BleConnection.initStart(true)
                            }, 3000)
                        }
                        R.id.tvDeviceDelete -> {
                            showWaitDialog("正在解除绑定")
                            BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                           // BLEManager.getInstance().dataDispatcher.clear(Hawk.get("address"))
                            BLEManager.getInstance().dataDispatcher.clearAll()

                            Handler(Looper.getMainLooper()).postDelayed({
                                var value = HashMap<String, String>()
                                value["mac"] =""
                                mViewModel.setUserInfo(value)
                                Hawk.put("address", "")
                                Hawk.put("name", "")
                                BleConnection.Unbind = true
                                Hawk.put("Unbind","MyDeviceActivity Unbind=true")
                                SNEventBus.sendEvent(Config.eventBus.DEVICE_DELETE_DEVICE)
                                hideWaitDialog()
                                finish()
                              //  RoomUtils.roomDeleteAll()
                                //   JumpUtil.startBleConnectActivity(this@MyDeviceActivity)
                            }, 2000)
                        }
                        R.id.settingCleanRoom -> {
                            AppUtils.clearAllCache(this@MyDeviceActivity)
                            settingCleanRoom.setContentText("0KB")
                        }
                        else -> {
                            //  BleWrite.writeDisconnectBluetoothShutdownCall()
                            // JumpUtil.startBleConnectActivity(this@MyDeviceActivity)
                        }
                    }
                    dialog.dismiss()


                }
                btnCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {

        when (event.code) {
            Config.eventBus.DEVICE_FIRMWARE -> {
                mDeviceFirmwareBean= event.data as DeviceFirmwareBean
                TLog.error("获取新设备+"+mDeviceFirmwareBean.toString())
            }
            Config.eventBus.DEVICE_OTA_UPDATE_COMPLETE->
            {
                mDeviceFirmwareBean=event.data as DeviceFirmwareBean
                settingUpdate.setContentText(mDeviceFirmwareBean.versionName)
            }
//            Config.eventBus.DEVICE_OTA_UPDATE->
//            {
//                TLog.error("=="+Hawk.get("address"))
//                TLog.error("=="+Hawk.get("name"))
//                BleConnection.startOTAActivity=false//不需要在ota的时候再次跳转
//                JumpUtil.startOTAActivity(this,Hawk.get("address")
//                    ,Hawk.get("name")
//                    ,mDeviceFirmwareBean.productNumber
//                    ,mDeviceFirmwareBean.version
//                ,true
//                )
//              //  showWaitDialog("下载更新文件中...")
//              //  FileUtil.delete("${ExcelUtil.filePath}/NFR52_OTA.zip")
//              //  mViewModel.findUpdate(mDeviceFirmwareBean.productNumber,mDeviceFirmwareBean.version)
//
//            }
        }

    }

}