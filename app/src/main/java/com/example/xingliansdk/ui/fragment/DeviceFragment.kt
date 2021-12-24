package com.example.xingliansdk.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import kotlinx.android.synthetic.main.fragment_device.*
import com.shon.bluetooth.BLEManager

class DeviceFragment : BaseFragment<MainViewModel>()
    , BleWrite.FirmwareInformationInterface
    , BleWrite.DevicePropertiesInterface
    , BleWrite.DeviceBloodPressureInterface
    , View.OnClickListener {
    override fun layoutId() = R.layout.fragment_device

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.setTitleBar(activity, titleBar)
//        SNEventBus.register(this)
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
            }

            override fun onActionImageClick() {
                if (Hawk.get<String>("address").isNullOrEmpty())
                    JumpUtil.startBleConnectActivity(activity)
                else
                    JumpUtil.startOtherSettingActivity(activity)

            }

            override fun onActionClick() {
            }
        })
        settingModuleMeasurement.setOnClickListener(this)
        settingBigDataInterval.setOnClickListener(this)
        settingReminderPush.setOnClickListener(this)
        settingUnbind.setOnClickListener(this)
        bleWrite()
        settingBloodPressureCalibration.setRightImage(R.mipmap.icon_zan_on)
//        tv_scan.setOnClickListener {
//            tv_scan.text = "扫描中..."
//            startScan()
//        }

    }

    private fun bleWrite() {
        Handler(Looper.getMainLooper()).postDelayed({
//            BleWrite.writeForGetFirmwareInformation(this)
//            BleWrite.writeForGetDeviceProperties(this)
//            BleWrite.writeForGetDeviceBloodPressure(this)
        }, 500)

    }

    override fun lazyLoadData() {
    }

    override fun createObserver() {
    }

    override fun onResult(
        productNumber: String?,
        versionName: String?,
        version: Int,
        nowMaC: String?,
        mac: String?
    ) {
        tvProductNumber.text = "产品编号:$productNumber"
        tvVersion.text = "固件版本号:$versionName"
        tvMac.text = "mac:$nowMaC"

    }

    override fun DevicePropertiesResult(
        power: Int?,
        currentBattery: Int,
        totalBattery: Int,
        type: Int
    ) {
        tvPower.text = "当前电量:$power"
        tvBattery.text = "展示格数:${(currentBattery / totalBattery.toFloat())}"
        if (type == 0)
            tvType.text = "运动上传:间隔上传"
        else
            tvType.text = "运动上传:实时上传"

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingModuleMeasurement -> {
                JumpUtil.startModuleMeasurementListActivity(activity)
            }
            R.id.settingBigDataInterval -> {
                JumpUtil.startBigDataIntervalActivity(activity)
            }
            R.id.settingReminderPush -> {

            }
            R.id.settingUnbind -> {
                showWaitDialog("正在解除绑定")
                BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                Handler(Looper.getMainLooper()).postDelayed({
                    Hawk.put("address", "")
                    ShowToast.showToastLong("address==null DeviceFragment")
                    BleConnection.Unbind = true
                    Hawk.put("Unbind","DeviceFragment Unbind=true")
                    hideWaitDialog()
                    JumpUtil.startBleConnectActivity(activity)
                }, 3000)
            }
        }
    }

    override fun onResult(value: String?, value1: String?) {
        settingBloodPressureCalibration.setContentText("$value-$value")
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEventReceived(event: SNEvent<Any>) {
//        when (event.code) {
//            Config.UNBIND_BLE -> {
//                var data = event.data as Boolean
//                if(data)
//                {
//                    hideWaitDialog()
//                    JumpUtil.startBleConnectActivity(activity)
//                }
//            }
//        }
//    }
}