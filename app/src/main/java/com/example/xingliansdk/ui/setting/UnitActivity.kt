package com.example.xingliansdk.ui.setting

import android.os.Bundle
import android.widget.CompoundButton
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.DataBean
import com.shon.connector.bean.DeviceInformationBean
import kotlinx.android.synthetic.main.activity_unit.*
import kotlinx.android.synthetic.main.item_unit.view.*

class UnitActivity : BaseActivity<UserViewModel>(),BleWrite.DeviceMotionInterface {
    override fun layoutId() = R.layout.activity_unit
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {
                if (BleConnection.iFonConnectError) {
                    ShowToast.showToastLong("蓝牙设备未连接,请前往重新连接")
                    return
                }
                Hawk.put(Config.database.PERSONAL_INFORMATION, mDeviceInformationBean)
                TLog.error("获取数据"+Gson().toJson(Hawk.get<DeviceInformationBean>(Config.database.PERSONAL_INFORMATION)))
                BleWrite.writeDeviceInformationCall(mDeviceInformationBean,true)
                BleWrite.writeForGetDeviceMotion(this@UnitActivity,true)
                var value = HashMap<String, String>()
                TLog.error("mDeviceInformationBean.unitSystem.toString()+="+mDeviceInformationBean.unitSystem.toString())
                value["distanceUnit"]=mDeviceInformationBean.unitSystem.toString()
                value["temperatureUnit"]=mDeviceInformationBean.temperatureSystem.toString()
                value["timeFormat"]=mDeviceInformationBean.timeSystem.toString()
                userInfo.userConfig.distanceUnit=mDeviceInformationBean.unitSystem.toInt()
                userInfo.userConfig.temperatureUnit=mDeviceInformationBean.temperatureSystem.toInt()
                userInfo.userConfig.timeFormat=mDeviceInformationBean.timeSystem.toInt()
                Hawk.put(Config.database.USER_INFO,userInfo)
                SNEventBus.sendEvent( Config.eventBus.CHANGE_UNIT, userInfo)
                mViewModel.setUserInfo(value!!)
                finish()
            }
        })
        initBind()
    }

    override fun createObserver() {
        super.createObserver()

    }
    lateinit var switchListener: CompoundButton.OnCheckedChangeListener
    private fun initBind() {
        switchListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            TLog.error("触发")
            // getChecked()
            when (buttonView) {//不知道为啥下面的逻辑是反着的
                includeDistance.rbUnitLeft -> {
                    mDeviceInformationBean.setUnitSystem(1)
                }
                includeDistance.rbUnitRight -> {
                    mDeviceInformationBean.setUnitSystem(0)
                }
                includeTemperature.rbUnitLeft -> {
                    mDeviceInformationBean.setTemperatureSystem(1)
                }
                includeTemperature.rbUnitRight -> {
                    mDeviceInformationBean.setTemperatureSystem(0)
                }
                includeTimeType.rbUnitLeft -> {
                    mDeviceInformationBean.setTimeSystem(1)
                }
                includeTimeType.rbUnitRight -> {
                    mDeviceInformationBean.setTimeSystem(0)
                }

            }
        }
        includeDistance.tvUnitTitle.text = "距离显示格式"
        includeDistance.rbUnitLeft.text = "km"
        includeDistance.rbUnitRight.text = "mile"
        includeDistance.rbUnitLeft.setOnCheckedChangeListener(switchListener)
        includeDistance.rbUnitRight.setOnCheckedChangeListener(switchListener)
        includeWeight.tvUnitTitle.text = "重量显示格式"
        includeWeight.rbUnitLeft.text = "KG"
        includeWeight.rbUnitRight.text = "LB"
        includeWeight.rbUnitLeft.setOnCheckedChangeListener(switchListener)
        includeWeight.rbUnitRight.setOnCheckedChangeListener(switchListener)
        includeTemperature.tvUnitTitle.text = "温度显示格式"
        includeTemperature.rbUnitLeft.text = "℃"
        includeTemperature.rbUnitRight.text = "℉"
        includeTemperature.rbUnitLeft.setOnCheckedChangeListener(switchListener)
        includeTemperature.rbUnitRight.setOnCheckedChangeListener(switchListener)

        includeTimeType.tvUnitTitle.text = "手环时间制式"
        includeTimeType.rbUnitLeft.text = "24"
        includeTimeType.rbUnitRight.text = "12"
        includeTimeType.rbUnitLeft.setOnCheckedChangeListener(switchListener)
        includeTimeType.rbUnitRight.setOnCheckedChangeListener(switchListener)
        TLog.error("n==${Gson().toJson(mDeviceInformationBean)}")
        TLog.error("mDeviceInformationBean.temperatureSystem.toInt()==${mDeviceInformationBean.temperatureSystem.toInt()}")
        if (mDeviceInformationBean.unitSystem.toInt() == 0) {
            includeDistance.rbUnitLeft.isChecked = true
            //     includeDistance.rbUnitRight.isChecked = false
        } else {
            includeDistance.rbUnitRight.isChecked = true
            //   includeDistance.rbUnitLeft.isChecked = false
        }
        if (mDeviceInformationBean.temperatureSystem.toInt() === 0) {
            TLog.error("左边" + mDeviceInformationBean.temperatureSystem.toInt())
            includeTemperature.rbUnitLeft.isChecked = true
            // includeTemperature.rbUnitRight.isChecked = false
        } else {
            TLog.error("右边")
            //   includeTemperature.rbUnitLeft.isChecked = false
            includeTemperature.rbUnitRight.isChecked = true
        }
        if (mDeviceInformationBean.timeSystem.toInt() == 0) {
            includeTimeType.rbUnitLeft.isChecked = true
        } else
            includeTimeType.rbUnitRight.isChecked = true
    }


    override fun DeviceMotionResult(mDataBean: DataBean?) {
        TLog.error("触发")
        SNEventBus.sendEvent(com.shon.connector.Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt(), mDataBean)
    }

}