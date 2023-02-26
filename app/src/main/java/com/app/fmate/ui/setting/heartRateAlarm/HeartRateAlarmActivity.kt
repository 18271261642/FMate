package com.app.fmate.ui.setting.heartRateAlarm
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.ui.login.viewMode.UserViewModel
import com.shon.connector.utils.ShowToast
import com.app.fmate.widget.TitleBarLayout
import com.github.iielse.switchbutton.SwitchView
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_heart_rate_alarm.*
import kotlinx.android.synthetic.main.item_switch.*

/**
 * 心率报警页面
 */
class HeartRateAlarmActivity : BaseActivity<UserViewModel>() {
    var num = 0
//    var data: HeartRateAlarmBean? = null
    var mSwitch=2
    var  value  = HashMap<String,String>()
    override fun layoutId() = R.layout.activity_heart_rate_alarm
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        img.visibility = View.GONE
        value["heartRateAlarm"] = userInfo.userConfig.heartRateAlarm
        value["heartRateThreshold"]=userInfo.userConfig.heartRateThreshold.toString()
        tv_name.text = resources.getString(R.string.string_device_heart_alarm)
        mSwitch= userInfo.userConfig.heartRateAlarm.toInt()



     //   data = Hawk.get(Config.database.HEART_RATE_ALARM, HeartRateAlarmBean(0, 0))
     //   num = data?.heartNum!!
            num=userInfo.userConfig.heartRateThreshold
        if (num > 0)
            edtHeartRate.setText(num.toString())
//        Switch.isOpened=data?.getmSwitch()==2
        Switch.isOpened=mSwitch==2

        edtHeartRate.isEnabled = Switch.isOpened
        if(Switch.isOpened){
            edtHeartRate.setTextColor(resources.getColor(R.color.color_heart))
            edtHeartRate.setBackgroundColor(Color.WHITE)
        }else{

            edtHeartRate.setTextColor(resources.getColor(R.color.gray1))
        }


        TLog.error("num==$num")
        Switch.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                Switch.isOpened = true
                value["heartRateAlarm"] ="2"
                edtHeartRate.isEnabled = true
                edtHeartRate.setTextColor(resources.getColor(R.color.color_heart))
                edtHeartRate.setBackgroundColor(Color.WHITE)
            }

            override fun toggleToOff(view: SwitchView?) {
                Switch.isOpened = false
                value["heartRateAlarm"] ="1"
                edtHeartRate.isEnabled = false
                edtHeartRate.setTextColor(resources.getColor(R.color.gray1))
            }
        })
        titleBar.setTitleBarListener(object :TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
            //    Hawk.put(Config.database.HEART_RATE_ALARM, data)
               TLog.error("===value=="+ value["heartRateThreshold"])
                TLog.error("===value=="+value["heartRateAlarm"])

                if(value["heartRateThreshold"] == null || value["heartRateThreshold"] =="0"){
                    ShowToast.showToastLong(resources.getString(R.string.string_heart_alarm_no_empty))
                    return
                }


                if(value["heartRateThreshold"]!!.toInt()>250||value["heartRateThreshold"]!!.toInt()<100)
                {
                    ShowToast.showToastLong(resources.getString(R.string.string_heart_alarm_normal_alert))
                    return
                }
                userInfo.userConfig.heartRateAlarm= value["heartRateAlarm"]
                userInfo.userConfig.heartRateThreshold= value["heartRateThreshold"]!!.toInt()

                Hawk.put(Config.database.USER_INFO, userInfo)
                BleWrite.writeHeartRateAlarmSwitchCall(
                    userInfo.userConfig.heartRateAlarm.toInt(),
                    userInfo.userConfig.heartRateThreshold)
                mViewModel.setUserInfo(value!!)
                finish()
            }

        })
        edtHeartRate.addTextChangedListener {
        TLog.error("it=="+it.toString())
            if(it.isNullOrEmpty())
                value["heartRateThreshold"] = 0.toString()
            else
                value["heartRateThreshold"] =it.toString()
        }
    }



    override fun createObserver() {
        super.createObserver()
    }
}