package com.example.xingliansdk.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.xingliansdk.Config.database.*
import com.example.xingliansdk.Config.eventBus.*
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.MeImgAdapter
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.bean.DevicePropertiesBean
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.DialImgBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.meView.MeViewModel
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.bean.DeviceInformationBean
import com.shon.connector.call.write.dial.DialGetAssignCall
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_device_information.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me.imgHead
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 我的页面
 */
class MeFragment : BaseFragment<MeViewModel>(), View.OnClickListener,
    BleWrite.DevicePropertiesInterface,
    BleWrite.FlashGetDialInterface {
    override fun layoutId() = R.layout.fragment_me
    var step = 0
    lateinit var meDialImgAdapter: MeImgAdapter
    private lateinit var mList: MutableList<String>

    override fun initView(savedInstanceState: Bundle?) {
        TLog.error("MeFragment initView")
        SNEventBus.register(this)
        setting_step.setOnClickListener(this)
//        settingStorageInterval.setOnClickListener(this)
        constBle.setOnClickListener(this)
        setting_unit.setOnClickListener(this)
        constInfo.setOnClickListener(this)
        tvDeviceAdd.setOnClickListener(this)
        tvReconnection.setOnClickListener(this)
        tvDele.setOnClickListener(this)
        setting_sleep.setOnClickListener(this)
        settingAbout.setOnClickListener(this)
        setting_help.setOnClickListener(this)
        settingSett.setOnClickListener(this)
        tvDial.setOnClickListener(this)
        var mStr = SpannableString(tvDeviceAdd.text.toString())
        mStr.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.sub_text_color)),
            0,
            6,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvDeviceAdd.text = mStr
        getBleStatus()
        if (userInfo != null && userInfo.user != null)
            tvEdtData.text = "我的ID:" + userInfo.user.userId
       // setting_step.setContentText(mDeviceInformationBean.exerciseSteps.toString())
        if (Hawk.get<Int>(SLEEP_GOAL) != null)
           // setting_sleep.setContentText(DateUtil.getTextTime(Hawk.get(SLEEP_GOAL)))

        setImgHead()
        setAdapter()
    }

    private fun setAdapter() {
        mList = ArrayList()
        ryDial.layoutManager = GridLayoutManager(activity, 3)
        mList.add("")
        mList.add("")
        mList.add("")
        meDialImgAdapter = MeImgAdapter(mList)
        ryDial.adapter = meDialImgAdapter
        meDialImgAdapter.notifyDataSetChanged()
    }

    private fun setImgHead() {
        mDeviceInformationBean = Hawk.get(
            PERSONAL_INFORMATION, DeviceInformationBean()
        )
        var userInfo = Hawk.get(USER_INFO, LoginBean())
        TLog.error("userInfo+=" + Gson().toJson(userInfo))
        var img = Hawk.get<String>(IMG_HEAD)
        if (userInfo.user == null)
            return
        tvPhone?.text = userInfo.user.nickname
        if (userInfo.user.headPortrait.isNotEmpty() && context?.let { HelpUtil.netWorkCheck(it) }!!) {
            TLog.error("头像==" + userInfo.user.headPortrait)
            ImgUtil.loadCircle(imgHead, userInfo.user.headPortrait)
            return
        }
        if (FileUtil.isFileExists(img)) {  //显示本地图片
            TLog.error("头像img==" + img)
            ImgUtil.loadCircle(imgHead, img)
        } else {
            Hawk.put(USER_INFO, userInfo)
            if (userInfo.user.sex == "1")
                ImgUtil.loadCircle(imgHead, R.mipmap.icon_head_man)
            else
                ImgUtil.loadCircle(imgHead, R.mipmap.icon_head_woman)
        }

    }


    private fun getBleStatus() {
        TLog.error("BleConnection.iFonConnectError==" + BleConnection.iFonConnectError)
        TLog.error("BleConnection.Unbind==" + BleConnection.Unbind)
        TLog.error("===" + Hawk.get<String>("name"))
        if (Hawk.get<String>("name")
                .isNullOrEmpty() && (BleConnection.iFonConnectError || BleConnection.Unbind)
        ) {
            llNoDevice?.visibility = View.VISIBLE
            llDeviceStatus?.visibility = View.GONE
        } else {
            llNoDevice?.visibility = View.GONE
            llDeviceStatus?.visibility = View.VISIBLE

        }
    }

    override fun createObserver() {
        mainViewModel.textValue.observe(viewLifecycleOwner, Observer {
            step = mainViewModel.getText()

            //    setting_step.setContentText(step.toString())
            // tvDeviceName.text = mainViewModel.getName()
        })
        mainViewModel.userInfo.observe(this) {
            TLog.error("it==" + Gson().toJson(it))
            if (it == null || it.user == null)
                return@observe
//            TLog.error("is=="+it)
//            setting_step.setContentText(it.userConfig.movingTarget)
//            setting_sleep.setContentText(DateUtil.getTextTime(it.userConfig.sleepTarget.toLong()))
            tvEdtData.text = "我的ID:" + it.user.userId
            setImgHead()
        }
        mainViewModel.result.observe(this)
        {
            if (it.user.nickname.isNullOrEmpty())
                tvPhone.text = it.user.phone
            else
                tvPhone.text = it.user.nickname
        }
        mViewModel.result.observe(this)
        {
            TLog.error("==" + Gson().toJson(it))
            var mImgList = Gson().fromJson(Gson().toJson(it), DialImgBean::class.java)
            TLog.error("mImgList==" + Gson().toJson(mImgList))
            if (mImgList.list.isNullOrEmpty() || mImgList.list.size <= 0)
                return@observe
            mList.clear()
            mList.addAll(mImgList.list)
            meDialImgAdapter.notifyDataSetChanged()
        }
        mViewModel.msg.observe(this)
        {
            TLog.error("没网==" + it)
            mList.clear()
            mList.add("")
            mList.add("")
            mList.add("")
            meDialImgAdapter.notifyDataSetChanged()
        }

    }

    override fun onClick(v: View) {
//        if (BleConnection.iFonConnectError) {
//            ShowToast.showToastLong("请前往链接蓝牙设备")
//            return
//        }
        when (v.id) {
            R.id.constInfo -> {

                JumpUtil.startDeviceInformationActivity(activity, false)
            }
            R.id.setting_step -> {
                JumpUtil.startSportsGoalActivity(activity)
            }
            R.id.setting_sleep -> {
                JumpUtil.startSleepGoalActivity(activity)
            }
            R.id.constBle,
            R.id.imgDevice,
            R.id.tvDeviceName
            -> {
                if (!turnOnBluetooth()) {
                    return
                }
                if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                    ShowToast.showToastLong("蓝牙设备未连接,请连接蓝牙设备")
                    return
                }
                getInformationPermissions(requireActivity(), object : CallBack {
                    override fun next() {
                        JumpUtil.startMyDeviceActivity(activity, electricity)
                    }
                })

            }
            R.id.setting_unit -> {
                if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                    ShowToast.showToastLong("蓝牙设备未连接")
                    return
                }
                JumpUtil.startUnitActivity(activity)
            }
            R.id.tvDeviceAdd -> {
                if (!turnOnBluetooth()) {
                    return
                }
                getInformationPermissions(requireActivity(), object : CallBack {
                    override fun next() {
                        JumpUtil.startBleConnectActivity(activity)
                    }
                })

            }
            R.id.tvReconnection -> {
                if (!turnOnBluetooth()) {
                    return
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    if (Hawk.get<String>("address").isNullOrEmpty()) {
                    } else
                        BLEManager.getInstance().disconnectDevice(Hawk.get<String>("address"))
//                    TLog.error("断开")
                }, 1000)

                TLog.error("==" + Hawk.get<String>("address"))
                if (Hawk.get<String>("address").isNullOrEmpty()
                    && !userInfo.user.mac.isNullOrEmpty()
                ) {
                    Hawk.put("address", userInfo.user.mac)
                    TLog.error("内部==" + userInfo.user.mac)
                }
                tvReconnection.text = "重新连接中.."
                tvDele.visibility = View.GONE
                BleConnection.initStart(Hawk.get(DEVICE_OTA, false), 3000)
            }
            R.id.tvDele -> {
                AllGenJIDialog.deleteDialog(childFragmentManager)
                //  deleteDialog()
            }
            R.id.settingAbout -> {
                JumpUtil.startAboutActivity(activity)
            }
            R.id.setting_help -> {
                JumpUtil.startProblemsFeedbackActivity(activity)
            }
            R.id.settingSett -> {
                JumpUtil.startSettingActivity(activity)
            }
            R.id.tvDial -> {
                if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                    ShowToast.showToastLong("蓝牙设备未连接,请连接蓝牙设备")
                    return
                }
                if (activity?.let { HelpUtil.netWorkCheck(it) } == false) {
                    ShowToast.showToastLong("暂无网络,不可使用")
                    return
                }
//                if(!DataDispatcher.callDequeStatus)
//                {
//                    ShowToast.showToastLong("正在同步其他数据,请稍后尝试")
//                    return
//                }
                JumpUtil.startDialMarketActivity(activity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // ShowToast.showToastLong("=${Hawk.get<String>("iFonConnectError")}  ${Hawk.get<String>("Unbind")} ${
        TLog.error("=${Hawk.get<String>("iFonConnectError")}  ${Hawk.get<String>("Unbind")}  ${Hawk.get("type",-1)}")
        // BleWrite.writeFlashGetDialCall(this)
        if(!BleConnection.iFonConnectError)
        BleWrite.writeForGetDeviceProperties(this, true)
    }

    var electricity: Int = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventResult(event: SNEvent<*>) {
        when (event.code) {
            DEVICE_CONNECT_NOTIFY -> {
                tvDeviceName?.text = Hawk.get("name")
                getBleStatus()
                ll_connect_status?.visibility = View.GONE
                Hawk.put("type", DEVICE_CONNECT_NOTIFY)
            }
            DEVICE_ELECTRICITY -> {
                electricity = event.data.toString().toInt()
                getBleStatus()
                if (tvDeviceElectricity != null) {
                    tvDeviceElectricity.text = "$electricity%"
                    tvDeviceStatus.text = "已连接"
                    tvDeviceName.text = Hawk.get("name")
                    imgDevice.setImageResource(R.mipmap.img_product_connect)
                    ll_connect_status.visibility = View.GONE
                    tvDeviceElectricity.visibility = View.VISIBLE
                    mViewModel.getDialImg()
                }
                Hawk.put("type", DEVICE_ELECTRICITY)
            }
            DEVICE_BLE_OFF,
            DEVICE_DISCONNECT -> {
                tvDeviceStatus?.text = "未连接"
                ll_connect_status?.visibility = View.VISIBLE
                tvDeviceElectricity?.visibility = View.GONE
                tvReconnection?.text = "重新连接"
                tvDele?.visibility = View.VISIBLE
                imgDevice?.setImageResource(R.mipmap.img_product_disconnect)
                mList.clear()
                mList.add("")
                mList.add("")
                mList.add("")
                meDialImgAdapter.notifyDataSetChanged()
                Hawk.put("type", DEVICE_DISCONNECT)
            }
            DEVICE_DELETE_DEVICE -> {
                mList.clear()
                mList.add("")
                mList.add("")
                mList.add("")
                meDialImgAdapter.notifyDataSetChanged()
                getBleStatus()
                Hawk.put("type", DEVICE_DELETE_DEVICE)
            }
            SPORTS_GOAL_SLEEP -> {
                setting_sleep.setContentText(DateUtil.getTextTime(Hawk.get(SLEEP_GOAL)))
            }
            SPORTS_GOAL_EXERCISE_STEPS -> {
                val step: String = event.data.toString()
               // setting_step.setContentText(step)
            }
            EVENT_BUS_IMG_HEAD -> {
                setImgHead()
            }
        }
    }

    override fun DevicePropertiesResult(
        electricity: Int,
        mCurrentBattery: Int,
        mDisplayBattery: Int,
        type: Int
    ) {
        TLog.error("估计数据返回了")
        Hawk.put(
            DEVICE_ATTRIBUTE_INFORMATION,
            DevicePropertiesBean(electricity, mCurrentBattery, mDisplayBattery, type)
        )
        SNEventBus.sendEvent(DEVICE_ELECTRICITY, electricity)
    }

    override fun onResultDialIdBean(bean: MutableList<DialGetAssignCall.DialBean>?) {
        //效验表盘
        if (HelpUtil.netWorkCheck(activity as Context)) {
            if (bean.isNullOrEmpty())
                return
            mViewModel.checkDialSate(Gson().toJson(bean))
        }
    }
}