package com.example.xingliansdk.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.TestNetActivity
import com.example.xingliansdk.Config.database.*
import com.example.xingliansdk.Config.eventBus.*
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.MeImgAdapter
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.bean.DeviceFirmwareBean
import com.example.xingliansdk.bean.DevicePropertiesBean
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.DialImgBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.meView.MeViewModel
import com.example.xingliansdk.ui.deviceconn.*
import com.example.xingliansdk.utils.*
import com.google.gson.Gson
import com.hjq.permissions.XXPermissions
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.bean.DeviceInformationBean
import com.shon.connector.call.write.dial.DialGetAssignCall
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_device_information.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_my_device.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me.imgHead
import kotlinx.android.synthetic.main.item_me_device_layout.*
import kotlinx.android.synthetic.main.item_me_watch_ring_layout.*
import kotlinx.android.synthetic.main.item_mine_connected_layout.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 我的页面
 */
class MeFragment : BaseFragment<MeViewModel>(), View.OnClickListener,
    BleWrite.DevicePropertiesInterface,
    BleWrite.FlashGetDialInterface {


    //展示已经连接的列表
    private var recordAdapter : MeConnectedDeviceAdapter?= null
    private var moreList : ArrayList<ConnectedDeviceBean> = arrayListOf()


    override fun layoutId() = R.layout.fragment_me
    var step = 0
    lateinit var meDialImgAdapter: MeImgAdapter
    private lateinit var mList: MutableList<String>

    override fun initView(savedInstanceState: Bundle?) {
        TLog.error("MeFragment initView")
        SNEventBus.register(this)

        initViews()

        initRecy()

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
            tvEdtData.text = resources.getString(R.string.string_dial_mine)+"ID:" + userInfo.user.userId
       // setting_step.setContentText(mDeviceInformationBean.exerciseSteps.toString())
       // if (Hawk.get<Int>(SLEEP_GOAL) != null)
           // setting_sleep.setContentText(DateUtil.getTextTime(Hawk.get(SLEEP_GOAL)))

        imgDevice?.alpha = 0.5f

        setImgHead()
        setAdapter()

        constInfo.setOnLongClickListener {
            startActivity(Intent(activity, TestNetActivity::class.java))
            true
        }
    }


    private fun initViews(){
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

        meMoreDeviceTv.setOnClickListener(this)
        meAddDeviceTv.setOnClickListener(this)
        itemMeAddDeviceTv.setOnClickListener(this)
        meHomeWatchCardView.setOnClickListener(this)
        meHomeRingCardView.setOnClickListener(this)
        meRingDeleteTv.setOnClickListener(this)
        meWatchDeleteTv.setOnClickListener(this)


        itemMeHolderRingView.alpha = 0.5f
    }

    private fun initRecy(){
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        meConnectedRy.layoutManager = linearLayoutManager

        recordAdapter = MeConnectedDeviceAdapter(moreList,activity)
        meConnectedRy.adapter = recordAdapter


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
        val userInfo = Hawk.get(USER_INFO, LoginBean())
        TLog.error("userInfo+=" + Gson().toJson(userInfo))
        var img = Hawk.get<String>(IMG_HEAD)
        if (userInfo.user == null)
            return
        tvPhone?.text = userInfo.user.nickname
        if (userInfo.user.headPortrait.isNotEmpty() && context?.let { HelpUtil.netWorkCheck(it) }!!) {
            TLog.error("头像==" + userInfo.user.headPortrait)
            ImgUtil.loadCircle(imgHead, userInfo.user.headPortrait,userInfo.user.sex == "1")
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
            tvEdtData.text = resources.getString(R.string.string_dial_mine)+"ID:" + it.user.userId
            setImgHead()
        }
        mainViewModel.result.observe(this)
        {
            if (it.user.nickname.isNullOrEmpty())
                tvPhone.text = "--"
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



        //获取连接的记录
        mViewModel.recordDeviceResult.observe(this){
            moreList.clear()
            TLog.error("-------记录返回="+Gson().toJson(it))
            if (it.list!= null) {
                moreList.addAll(it.list)
            }

            TLog.error("-----222--记录返回="+Gson().toJson(moreList))

            if(moreList.size == 0){
                meHolderLayout.visibility = View.GONE
                meConnectEmptyLayout.visibility = View.VISIBLE
                meNoDeviceLayout.visibility = View.VISIBLE
                meConnectedRy.visibility = View.GONE
            }else{
                meConnectEmptyLayout.visibility = View.GONE
                meHolderLayout.visibility = View.VISIBLE
                operateBind()
            }

        }


        //删除连接的记录
        mViewModel.deleteRecord.observe(this){
            TLog.error("-----删除连接记录="+Gson().toJson(it))
            //获取连接的记录
            mViewModel.getConnRecordDevice()
        }

        mViewModel.deleteMsg.observe(this){
            TLog.error("-error----删除连接记录="+Gson().toJson(it))
        }



    }


    //处理显示绑定的逻辑
    private fun operateBind(){
        moreList.forEach {
            if(it.productName.contains("Ring") && moreList.size == 1){    //只有戒指
                //把另一个隐藏掉
                meHomeWatchCardView.visibility = View.GONE
                //戒指没有表盘市场
                watchDialCarView.visibility = View.GONE
                return
            }

            if(it.productName.contains("GT") && moreList.size == 1){    //只有手表
                meHomeRingCardView.visibility = View.GONE
                watchDialCarView.visibility = View.VISIBLE
                return
            }

        }
    }


    override fun onClick(v: View) {
//        if (BleConnection.iFonConnectError) {
//            ShowToast.showToastLong("请前往链接蓝牙设备")
//            return
//        }
        when (v.id) {

            R.id.meRingDeleteTv->{  //删除戒指的记录，传Mac，删除后重新更新UI
                wearDialog(R.id.meRingDeleteTv)
            }
            R.id.meWatchDeleteTv->{ //删除手表记录，传Mac，删除后更新UI
                wearDialog(R.id.meWatchDeleteTv)
            }


            R.id.meMoreDeviceTv->{  //更多设备
                startActivity(Intent(activity,MoreConnectActivity::class.java))
            }
            R.id.meAddDeviceTv,
            R.id.itemMeAddDeviceTv->{   //添加设备
                startActivity(Intent(activity,AddDeviceSelectActivity::class.java))
            }


            R.id.constInfo -> {
//                startActivity(Intent(activity,TestNetActivity::class.java))
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
                R.id.meHomeRingCardView,
                R.id.meHomeWatchCardView
            -> {
                if (!turnOnBluetooth()) {
                    return
                }
                if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
                    return
                }

                if(!XingLianApplication.mXingLianApplication.getDeviceConnStatus()){
                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
                    return
                }

                JumpUtil.startMyDeviceActivity(activity, electricity)

//                getInformationPermissions(requireActivity(), object : CallBack {
//                    override fun next() {
//                        JumpUtil.startMyDeviceActivity(activity, electricity)
//                    }
//                })

            }
            R.id.setting_unit -> {
                if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
                    return
                }
                JumpUtil.startUnitActivity(activity)
            }
            R.id.tvDeviceAdd -> {
                if (!turnOnBluetooth()) {
                    return
                }
                JumpUtil.startBleConnectActivity(activity)

//                getInformationPermissions(requireActivity(), object : CallBack {
//                    override fun next() {
//                        JumpUtil.startBleConnectActivity(activity)
//                    }
//                })

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


                XXPermissions.with(this).permission(android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).request { permissions, all ->
                    tvReconnection.text = resources.getString(R.string.string_conn_ing)
                    tvDele.visibility = View.GONE
                    BleConnection.initStart(Hawk.get(DEVICE_OTA, false), 3000)
                }


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
                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
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
        try {
            // ShowToast.showToastLong("=${Hawk.get<String>("iFonConnectError")}  ${Hawk.get<String>("Unbind")} ${
         //   TLog.error("=${Hawk.get<String>("iFonConnectError")}  ${Hawk.get<String>("Unbind")}  ${Hawk.get("type",-1)}")
            // BleWrite.writeFlashGetDialCall(this)

            //获取连接的记录
            mViewModel.getConnRecordDevice()

            if(!BleConnection.iFonConnectError && XingLianApplication.mXingLianApplication.getDeviceConnStatus())
                BleWrite.writeForGetDeviceProperties(this, true)
        }catch (e : Exception){
            e.printStackTrace()
        }

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
                    //获取连接的记录
                    mViewModel.getConnRecordDevice()

                    //已经连接了，删除隐藏，电量显示
                    meWatchDeleteTv.visibility = View.GONE
                    meRingDeleteTv.visibility = View.GONE

                    meConnRingBatteryLayout.visibility = View.VISIBLE


                    itemMeHomeRingStatusTv.text  =    resources.getString(R.string.string_connected)
                    itemMeRingBatteryValue.text =  "$electricity%"
                    itemMeConnectBatteryValue.text = "$electricity%"


                    tvDeviceStatus.text = resources.getString(R.string.string_connected)
                    tvDeviceName.text = Hawk.get("name")
                    imgDevice.setImageResource(R.mipmap.img_product_connect)
                    imgDevice.alpha = 1f
                    ll_connect_status.visibility = View.GONE
                    tvDeviceElectricity.visibility = View.VISIBLE
                    var bean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
                    if(bean!=null && bean.productNumber!=null)
                    mViewModel.getDialImg(bean.productNumber)
                }
                Hawk.put("type", DEVICE_ELECTRICITY)
            }
            DEVICE_BLE_OFF,
            DEVICE_DISCONNECT -> {

                meConnRingBatteryLayout.visibility = View.GONE
                meRingDeleteTv.visibility = View.VISIBLE

                itemMeHomeRingStatusTv.text  =    resources.getString(R.string.string_no_conn)
                itemMeHomeStatusTv.text = resources.getString(R.string.string_no_conn)


                tvDeviceStatus?.text = resources.getString(R.string.string_no_conn)
                ll_connect_status?.visibility = View.VISIBLE
                tvDeviceElectricity?.visibility = View.GONE
                tvReconnection?.text =resources.getString(R.string.string_mine_re_conn)
                tvDele?.visibility = View.VISIBLE
               // imgDevice?.setImageResource(R.mipmap.img_product_disconnect)
                if(activity == null || requireActivity().isFinishing)
                    return
                imgDevice.setImageResource(R.mipmap.img_product_connect)
                imgDevice?.alpha = 0.5f

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
               // setting_sleep.setContentText(DateUtil.getTextTime(Hawk.get(SLEEP_GOAL)))
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

        Log.e("校验表盘","--------onResultDialIdBean="+Gson().toJson(bean))

        //效验表盘
        if (HelpUtil.netWorkCheck(activity as Context)) {
            if (bean.isNullOrEmpty())
                return
            mViewModel.checkDialSate(Gson().toJson(bean))
        }
    }


    //删除提醒
    private fun wearDialog(id: Int) {
        activity?.let {
            newGenjiDialog {
                layoutId = R.layout.alert_dialog_login
                dimAmount = 0.3f
                isFullHorizontal = true
                isFullVerticalOverStatusBar = false
                gravity = DialogGravity.CENTER_CENTER
                animStyle = R.style.BottomTransAlphaADAnimation
                convertListenerFun { holder, dialog ->
                    val btnOk = holder.getView<TextView>(R.id.dialog_confirm)
                    val btnCancel = holder.getView<TextView>(R.id.dialog_cancel)
                    val tvTitle = holder.getView<TextView>(R.id.tv_title)
                    val dialogContent = holder.getView<TextView>(R.id.dialog_content)
                    tvTitle?.text = resources.getString(R.string.string_text_remind)
                    when (id) {

                        R.id.meRingDeleteTv,
                        R.id.meWatchDeleteTv -> {
                            dialogContent?.text =
                                resources.getString(R.string.content_delete_device)
                        }

                    }
                    btnOk?.setOnClickListener {
                        when (id) {

                            R.id.meRingDeleteTv,
                            R.id.meWatchDeleteTv -> {
                                showWaitDialog(resources.getString(R.string.string_unbind_ing))
                                val connMac = Hawk.get("address","")
                                BLEManager.getInstance().disconnectDevice(connMac)
                                // BLEManager.getInstance().dataDispatcher.clear(Hawk.get("address"))
                                BLEManager.getInstance().dataDispatcher.clearAll()

                                Hawk.put("ELECTRICITY_STATUS", -1)

                                var deleteMac : String ?=null

                                //获取Mac
                                if(R.id.meRingDeleteTv == id){
                                    moreList.forEach {
                                        if(it.productName.toLowerCase(Locale.ROOT).contains("ring")){
                                            deleteMac = it.mac
                                        }
                                    }
                                }

                                if(id == R.id.meWatchDeleteTv){
                                    moreList.forEach {
                                        if(it.productName.toLowerCase(Locale.ROOT).contains("gt")){
                                            deleteMac = it.mac
                                        }
                                    }
                                }


                                Handler(Looper.getMainLooper()).postDelayed({
                                    val value = HashMap<String, String>()
                                    value["mac"] = ""
                                    mViewModel.setUserInfo(value)
                                    deleteMac?.toLowerCase(Locale.ROOT)
                                        ?.let { it1 -> mViewModel.deleteRecordByMac(it1) }

                                    Hawk.put("address", "")
                                    Hawk.put("name", "")
                                    BleConnection.Unbind = true
                                    Hawk.put("Unbind", "MyDeviceActivity Unbind=true")
                                    SNEventBus.sendEvent(DEVICE_DELETE_DEVICE)
                                    hideWaitDialog()

                                    //  RoomUtils.roomDeleteAll()
                                    //   JumpUtil.startBleConnectActivity(this@MyDeviceActivity)
                                }, 2000)
                            }

                        }
                        dialog.dismiss()


                    }
                    btnCancel?.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }.showOnWindow(it.supportFragmentManager)
        }
    }
}