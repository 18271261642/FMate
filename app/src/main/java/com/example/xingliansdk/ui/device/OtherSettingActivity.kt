package com.example.xingliansdk.ui.device
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.Config
import kotlinx.android.synthetic.main.activity_other_setting.*
import com.example.xingliansdk.bean.SwitchBean as SwitchBean

class OtherSettingActivity : BaseActivity<BaseViewModel>(), View.OnClickListener {
//    lateinit var mOtherSwitchAdapter: OtherSwitchAdapter
    lateinit var mList: ArrayList<SwitchBean>
    override fun layoutId() = R.layout.activity_other_setting
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        setAdapter()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingReset -> {
                dialog(R.id.settingReset)

            }
            R.id.settingDisconnectBluetoothOffDevice -> {
                dialog(R.id.settingDisconnectBluetoothOffDevice)
            }
        }
    }

    fun setAdapter() {
        if (Hawk.get<ArrayList<SwitchBean>>("switch").isNullOrEmpty()) {
            mList = ArrayList()
            mList.add(SwitchBean(Config.ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_REAL_TIME_BLOOD_OXYGEN_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_REAL_TIME_BLOOD_PRESSURE_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_REAL_TIME_TEMPERATURE_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_FIND_DEVICE_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_DO_NOT_DISTURB_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_DEVICE_CAMERA_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_HAND_BRIGHT_SCREEN_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_DRINK_WATER_REMINDER_SWITCH_KEY, 2))
            mList.add(SwitchBean(Config.ControlClass.APP_SEDENTARY_REMINDER_KEY, 2))
        } else
            mList = Hawk.get<ArrayList<SwitchBean>>("switch")
        recyclerView.layoutManager = LinearLayoutManager(
            this@OtherSettingActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
//        mOtherSwitchAdapter = OtherSwitchAdapter(mList)
//        recyclerView.adapter = mOtherSwitchAdapter
//        mOtherSwitchAdapter.addChildClickViewIds(R.id.SwitchAlarmClock)
//        mOtherSwitchAdapter.setOnItemChildClickListener { _, view, position ->
//            when (view.id) {
//                R.id.SwitchAlarmClock -> {
//                    if (mList[position].switch == 2)
//                        mList[position].switch = 1
//                    else
//                        mList[position].switch = 2
//                    Hawk.put("switch", mList)
//                    BleWrite.writeHeartRateSwitchCall(
//                        mList[position].key,
//                        mList[position].switch.toByte()
//                    )
//                    TLog.error("=${position}")
//                }
//            }
//        }
    }

    private fun dialog(id: Int) {
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
                if (id == R.id.settingReset)
                    dialogContent?.text = resources.getString(R.string.content_want_reset)
                else
                    dialogContent?.text = resources.getString(R.string.content_want_disconnect)
                btnOk?.setOnClickListener {
                    if (id == R.id.settingReset)
                        BleWrite.writeFactoryRestorationResetCall()
                    else {
                        BleWrite.writeDisconnectBluetoothShutdownCall()
                        JumpUtil.startBleConnectActivity(this@OtherSettingActivity)
                    }
                    dialog.dismiss()
                    // Hawk.put("address","")

                }
                btnCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    fun switchBean() {

    }
}