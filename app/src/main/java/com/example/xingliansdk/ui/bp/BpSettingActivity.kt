package com.example.xingliansdk.ui.bp

import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout.TitleBarListener
import com.github.iielse.switchbutton.SwitchView
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.HawConstant
import com.shon.connector.BleWrite
import com.shon.connector.bean.AutoBpStatusBean
import com.shon.connector.utils.ShowToast
import kotlinx.android.synthetic.main.activity_bp_set_layout.*
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Admin
 *Date 2022/5/30
 */
class BpSettingActivity : BaseActivity<UserViewModel>() {


    private var autoBpStatusBean: AutoBpStatusBean? = null

    override fun layoutId(): Int {
        return R.layout.activity_bp_set_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()


        autoBpNormalSwitch.setOnStateChangedListener(onStateChangedListener)
        autoBpNightSwitch.setOnStateChangedListener(onStateChangedListener)

        titleBar.setTitleBarListener(object : TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {}
            override fun onActionClick() {
                setBpStatus()
            }
        })

        initData()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this){
            hideWaitDialog()
            ShowToast.showToastShort(resources.getString(R.string.string_save_successed))
            finish()
        }

        mViewModel.msg.observe(this){
            hideWaitDialog()
            ShowToast.showToastShort(resources.getString(R.string.string_save_failed))
        }

    }


    private fun initData() {
        if (autoBpStatusBean == null) autoBpStatusBean = AutoBpStatusBean()


        val saveBp = HawConstant.getAutoBpStatusData()

        val userInfo = Hawk.get(com.example.xingliansdk.Config.database.USER_INFO, LoginBean())
        if(userInfo != null){
            val isNormal = userInfo.userConfig.bloodPressureDaytimeMeasurement
            val isNight = userInfo.userConfig.bloodPressureNightMeasurement
            autoBpNightSwitch.isOpened = isNight == 0x02
            autoBpNormalSwitch.isOpened =isNormal == 0x02
        }else{
            autoBpNightSwitch.isOpened = false
            autoBpNormalSwitch.isOpened = false
        }
    }

    private val onStateChangedListener: SwitchView.OnStateChangedListener =
        object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView) {
                if (view.id == R.id.autoBpNightSwitch) { //夜间
                    autoBpNightSwitch.isOpened = true
                    autoBpStatusBean?.nightBpStatus = 0x02.toByte()
                }
                if (view.id == R.id.autoBpNormalSwitch) {    //正常
                    autoBpNormalSwitch.isOpened = true
                    autoBpStatusBean?.normalBpStatus = 0x02.toByte()
                }
            }

            override fun toggleToOff(view: SwitchView) {
                if (view.id == R.id.autoBpNightSwitch) { //夜间
                    autoBpNightSwitch.isOpened = false
                    autoBpStatusBean?.nightBpStatus = 0x01.toByte()
                }
                if (view.id == R.id.autoBpNormalSwitch) {    //正常
                    autoBpNormalSwitch.isOpened = false
                    autoBpStatusBean?.normalBpStatus = 0x01.toByte()
                }
            }
        }


    private fun setBpStatus() {
        if (autoBpNormalSwitch.isOpened) {
            autoBpStatusBean?.setNormalBpStatus(0x02.toByte())

        }else{
            autoBpStatusBean?.setNormalBpStatus(0x01.toByte())
        }
        autoBpStatusBean?.setStartHour(0x08)
        autoBpStatusBean?.setStartMinute(0x00)
        autoBpStatusBean?.setEndHour(0x17)
        autoBpStatusBean?.setEndMinute(0x3B)
        autoBpStatusBean?.setBpInterval(0x0A)

        //夜间
        autoBpStatusBean?.setNightBpStatus(if(autoBpNightSwitch.isOpened) 0x02.toByte() else 0x01)
        HawConstant.saveAutoBpData(autoBpStatusBean)
        showLoading("Loading...")
        BleWrite.writeSetAutoBpMeasureStatus(
            true, autoBpStatusBean
        ) { `object` ->
            if (`object` as Boolean) {
                val booleanList: MutableList<Boolean> =
                    ArrayList()
                booleanList.add(autoBpNormalSwitch.isOpened)
                booleanList.add(autoBpNightSwitch.isOpened)
                HawConstant.saveAutoBpStatus(booleanList)
                //                    ShowToast.INSTANCE.showToast(BpSettingActivity.this,"设置成功",2);
            }
        }
        val value = HashMap<String, String>()
        value["nickname"] = mDeviceInformationBean.name
        value["height"] = mDeviceInformationBean.height.toString() + ""
        value["weight"] = mDeviceInformationBean.weight.toString() + ""
        value["createTime"] = (System.currentTimeMillis() / 1000).toString() + ""
        value["age"] = mDeviceInformationBean.age.toString() + ""
        value["sex"] = mDeviceInformationBean.sex.toString() + ""
        value["birthDate"] = DateUtil.getDate(DateUtil.YYYY_MM_DD, mDeviceInformationBean.birth)

        value["bloodPressureNightMeasurement"] = if (autoBpNightSwitch.isOpened) "2" else "1"
        value["bloodPressureDaytimeMeasurement"] = if (autoBpNormalSwitch.isOpened) "2" else "1"
        mViewModel.setUserInfo(value)
    }
}