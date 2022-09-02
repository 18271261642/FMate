package com.example.xingliansdk.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.TestNetActivity
import com.example.xingliansdk.Config
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


        recordAdapter?.setOnMeAdapterItemClick(object : MeConnectedDeviceAdapter.OnMeAdapterItemClick{
            override fun onItemClick(position: Int) {
                if (!turnOnBluetooth()) {
                    return
                }
//                if (BleConnection.iFonConnectError || BleConnection.Unbind) {
//                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
//                    return
//                }
//
//                if(!XingLianApplication.mXingLianApplication.getDeviceConnStatus()){
//                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
//                    return
//                }
                if(!moreList.get(position).isConnected){
                    ShowToast.showToastLong(resources.getString(R.string.string_no_conn_desc))
                    return
                }

                JumpUtil.startMyDeviceActivity(activity, electricity,moreList.get(position).productCategoryId)
            }

            //重新连接
            override fun onItemReConnClick(position: Int) {
                toRetryConnDevice(moreList[position].mac)
            }

            override fun onItemDeleteClick(position: Int) {
                wearDialog(R.id.meRingDeleteTv,position)
            }

        })

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

            if(moreList.size == 0){
                watchDialCarView.visibility = View.VISIBLE
                meMoOperateLayout.visibility = View.GONE
                meConnectedRy.visibility = View.GONE
                meNoDeviceLayout.visibility = View.VISIBLE
            }else{
                meMoOperateLayout.visibility = View.VISIBLE
                meConnectedRy.visibility = View.VISIBLE
                meNoDeviceLayout.visibility = View.GONE
            }

            recordAdapter?.notifyDataSetChanged()

            setDeviceConn()

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


    override fun onClick(v: View) {
//        if (BleConnection.iFonConnectError) {
//            ShowToast.showToastLong("请前往链接蓝牙设备")
//            return
//        }
        when (v.id) {

//            R.id.meRingDeleteTv->{  //删除戒指的记录，传Mac，删除后重新更新UI
//                wearDialog(R.id.meRingDeleteTv)
//            }
//            R.id.meWatchDeleteTv->{ //删除手表记录，传Mac，删除后更新UI
//                wearDialog(R.id.meWatchDeleteTv)
//            }


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


    //显示连接状态，每次只能有一个连接
    private fun setDeviceConn(){
        if(moreList.isEmpty())
            return

        //遍历集合，与已经连接的Mac地址比对，判断哪个连接
        val saveAddress = Hawk.get("address","")
        if(TextUtils.isEmpty(saveAddress))
            return

        //判断是否要显示表盘市场，只有戒指连接时不显示表盘，其它都显示表盘
        var isShowDial = true
//        moreList.forEach {
//            TLog.error("-555555-记录返回="+(it.productCategoryId == 2)+" "+it.productCategoryId)
//            if(it.productCategoryId == 2){
//                isShowDial = true
//            }
//        }

        moreList.forEach {

            if(it.productCategoryId == 1 && it.mac.equals(saveAddress, ignoreCase = true) && XingLianApplication.getXingLianApplication().getDeviceConnStatus()
            ){
                isShowDial = false
            }
        }


        meAddDeviceTv.visibility = if(moreList.size==1) View.GONE else View.VISIBLE

        TLog.error("------是否需要显示表盘="+isShowDial)
        watchDialCarView.visibility = if(isShowDial) View.VISIBLE else View.GONE
        //如果戒指连接了就不显示表盘市场


//        //如果未连接就全部显示未连接
//        if(!XingLianApplication.getXingLianApplication().getDeviceConnStatus()){
//            moreList.forEachIndexed { index, connectedDeviceBean ->
//                connectedDeviceBean.connstatusEnum = ConnstatusEnum.NO_CONNECTED
//                connectedDeviceBean.isConnected = false
//            }
//
//            recordAdapter?.notifyDataSetChanged()
//            return
//        }



        moreList.forEachIndexed { index, connectedDeviceBean ->
            connectedDeviceBean.isConnected =
                connectedDeviceBean.mac.equals(saveAddress, ignoreCase = true) && XingLianApplication.getXingLianApplication().getDeviceConnStatus()
            if(connectedDeviceBean.isConnected){
                connectedDeviceBean.connstatusEnum = ConnstatusEnum.CONNECTED
                connectedDeviceBean.battery = electricity
            }else{
                connectedDeviceBean.battery = 0
            }

        }

        TLog.error("------更改状态="+Gson().toJson(moreList))

        recordAdapter?.notifyDataSetChanged()

    }


    var electricity: Int = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventResult(event: SNEvent<*>) {
        when (event.code) {

            -10->{  //更新成功
                TLog.error("-------更新成功=======")
                //获取连接的记录
                mViewModel.getConnRecordDevice()
            }

            DEVICE_CONNECT_NOTIFY -> {
                tvDeviceName?.text = Hawk.get("name")
                getBleStatus()
                ll_connect_status?.visibility = View.GONE
                Hawk.put("type", DEVICE_CONNECT_NOTIFY)
            }
            DEVICE_ELECTRICITY -> {
                electricity = event.data.toString().toInt()
                TLog.error("---------电量="+electricity)
                getBleStatus()
                if (tvDeviceElectricity != null) {

                    setDeviceConn()

                    tvDeviceElectricity.text = "$electricity%"

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

                setDeviceConn()

//                meConnRingBatteryLayout.visibility = View.GONE
//                meRingDeleteTv.visibility = View.VISIBLE
//
//                itemMeHomeRingStatusTv.text  =    resources.getString(R.string.string_no_conn)
//                itemMeHomeStatusTv.text = resources.getString(R.string.string_no_conn)


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
    private fun wearDialog(id: Int,position : Int) {
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

                                var deleteMac : String ?=null
                                //获取Mac
                                deleteMac = moreList[position].mac
                                //判断是否是解绑当前连接的设备
                                val connMac = Hawk.get("address","")
                                if(deleteMac.equals(connMac, ignoreCase = true)){
                                    BLEManager.getInstance().disconnectDevice(connMac.toUpperCase(
                                        Locale.ROOT))
                                    // BLEManager.getInstance().dataDispatcher.clear(Hawk.get("address"))
                                    BLEManager.getInstance().dataDispatcher.clearAll()

                                    Hawk.put("ELECTRICITY_STATUS", -1)
                                    Hawk.put("address", "")
                                    Hawk.put("name", "")
                                    BleConnection.Unbind = true
                                    Hawk.put("Unbind", "MyDeviceActivity Unbind=true")
                                    SNEventBus.sendEvent(DEVICE_DELETE_DEVICE)
                                }


                                Handler(Looper.getMainLooper()).postDelayed({
                                    val value = HashMap<String, String>()
                                    value["mac"] = ""
                                    if(moreList.size == 1){
                                        mViewModel.setUserInfo(value)
                                    }
//                                    mViewModel.setUserInfo(value)
                                    deleteMac?.toLowerCase(Locale.ROOT)
                                        ?.let { it1 -> mViewModel.deleteRecordByMac(it1) }

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


    //重新连接
    fun toRetryConnDevice(reMac : String){
        if (!turnOnBluetooth()) {
            return
        }

        val saveMac = Hawk.get("address","")
        if(!TextUtils.isEmpty(saveMac)){
            BLEManager.getInstance().disconnectDevice(saveMac)
            XingLianApplication.getXingLianApplication().setDeviceConnectedStatus(false)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (Hawk.get<String>("address").isNullOrEmpty()
                && reMac.isEmpty()
            ) {
                Hawk.put("address", reMac)
                TLog.error("内部==" + userInfo.user.mac)
            }
            Hawk.put("address", reMac)

            XXPermissions.with(this).permission(android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).request { permissions, all ->
                //  tvReconnection.text = resources.getString(R.string.string_conn_ing)

                moreList.forEach {
                    if(it.mac.equals(reMac, ignoreCase = true)){
                        it.connstatusEnum = ConnstatusEnum.CONNECTING
                    }
                }
                recordAdapter?.notifyDataSetChanged()
                tvDele.visibility = View.GONE
                BleConnection.initStart(Hawk.get(DEVICE_OTA, false), 3000)
            }
        },1000)


//        Handler(Looper.getMainLooper()).postDelayed({
//            if (Hawk.get<String>("address").isNullOrEmpty()) {
//            } else
//                BLEManager.getInstance().disconnectDevice(Hawk.get<String>("address"))
////                    TLog.error("断开")
//        }, 1000)

        TLog.error("-------重连==" + Hawk.get<String>("address"))

    }
}