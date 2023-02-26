package com.app.fmate.ui.deviceconn

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.DevicePropertiesBean
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.moreDevice.ConnectRecordViewModel
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.XXPermissions
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_blood_pressure.titleBar
import kotlinx.android.synthetic.main.activity_more_connect_layout.*
import kotlinx.android.synthetic.main.fragment_me.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 已经连接的更多设备
 * Created by Admin
 *Date 2022/7/27
 */
class MoreConnectActivity : BaseActivity<ConnectRecordViewModel>(){


    private var recordAdapter : MoreConnectedDeviceAdapter ?= null
    private var moreList : ArrayList<ConnectedDeviceBean> = arrayListOf()



    override fun layoutId(): Int {
        return R.layout.activity_more_connect_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        SNEventBus.register(this)

        moreRecordAddDeviceTv.setOnClickListener(){
            startActivity(Intent(this@MoreConnectActivity,AddDeviceSelectActivity::class.java))
        }

        initRecyclerView()

        initData()

    }

    private fun initRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        moreDeviceRecyclerView.layoutManager = linearLayoutManager


        recordAdapter = MoreConnectedDeviceAdapter(moreList,this)
        moreDeviceRecyclerView.adapter = recordAdapter

        recordAdapter!!.setOnMoreConnDeleteListener(object : MoreConnectedDeviceAdapter.OnMoreConnDeleteListener{
            override fun deleteItem(position: Int) {
                wearDialog(0,moreList.get(position).mac)
            }

            override fun reConnItem(position: Int) {
                toRetryConnDevice(moreList[position].mac)
            }

        })

    }



    private fun initData(){
        mViewModel.getConnRecordDevice()

    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.recordDeviceResult.observe(this){
            TLog.error("-------data="+it.list.toString())
            moreList.clear()
            if (it.list!= null) {
                moreList.addAll(it.list)
            }
            setDeviceConn()
        }

    }


    private fun setDeviceConn(){
        if(moreList.isEmpty())
            return
        if(!XingLianApplication.getXingLianApplication().getDeviceConnStatus()){
            moreList.forEach {
                it.connstatusEnum = ConnstatusEnum.NO_CONNECTED
                it.isConnected = false
                it.battery = 0
            }
            recordAdapter?.notifyDataSetChanged()
            return
        }

        val connMac = Hawk.get("address","")
        val devicePropertiesBean =  Hawk.get(
            Config.database.DEVICE_ATTRIBUTE_INFORMATION,
            DevicePropertiesBean(0, 0, 0, 0)
        )
        moreList.forEach {
            it.isConnected = it.mac==connMac.toLowerCase(Locale.ROOT)
            it.connstatusEnum = ConnstatusEnum.CONNECTED
            it.battery = devicePropertiesBean.electricity
        }


        recordAdapter?.notifyDataSetChanged()
    }

    //删除提醒
    private fun wearDialog(id: Int,mac : String) {
        this.let {
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

                       0 -> {
                            dialogContent?.text =
                                resources.getString(R.string.content_delete_device)
                        }

                    }
                    btnOk?.setOnClickListener {
                        when (id) {

                            0 -> {
                                showWaitDialog(resources.getString(R.string.string_unbind_ing))

                                val connMac = Hawk.get("address", "")
                                if(connMac.equals(mac, ignoreCase = true)){   //删除当前绑定的设备

                                    BLEManager.getInstance().disconnectDevice(mac.toUpperCase(Locale.ROOT))
                                    // BLEManager.getInstance().dataDispatcher.clear(Hawk.get("address"))
                                    BLEManager.getInstance().dataDispatcher.clearAll()
                                    Hawk.put("ELECTRICITY_STATUS", -1)
                                    Hawk.put("address", "")
                                    Hawk.put("name", "")
                                    BleConnection.Unbind = true
                                    Hawk.put("Unbind", "MyDeviceActivity Unbind=true")
                                    SNEventBus.sendEvent(Config.eventBus.DEVICE_DELETE_DEVICE)

                                }

                                Handler(Looper.getMainLooper()).postDelayed({
                                    val value = HashMap<String, String>()
                                    value["mac"] = ""
                                    if(moreList.size == 0){
                                        mViewModel.setUserInfo(value)
                                    }
                                   // mViewModel.setUserInfo(value)
                                    mViewModel.deleteRecordByMac(mac.toLowerCase(Locale.ROOT))

                                    hideWaitDialog()
                                    mViewModel.getConnRecordDevice()
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
            }.showOnWindow(supportFragmentManager)
        }
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

            Config.eventBus.DEVICE_CONNECT_NOTIFY -> {

                Hawk.put("type", Config.eventBus.DEVICE_CONNECT_NOTIFY)
            }
            Config.eventBus.DEVICE_ELECTRICITY -> {
                electricity = event.data.toString().toInt()
                TLog.error("---------电量="+electricity)

                if (tvDeviceElectricity != null) {
                    setDeviceConn()
                }
                Hawk.put("type", Config.eventBus.DEVICE_ELECTRICITY)
            }
            Config.eventBus.DEVICE_BLE_OFF,
            Config.eventBus.DEVICE_DISCONNECT -> {

                setDeviceConn()
                Hawk.put("type", Config.eventBus.DEVICE_DISCONNECT)
            }
            Config.eventBus.DEVICE_DELETE_DEVICE -> {

                Hawk.put("type", Config.eventBus.DEVICE_DELETE_DEVICE)
            }
            Config.eventBus.SPORTS_GOAL_SLEEP -> {
                // setting_sleep.setContentText(DateUtil.getTextTime(Hawk.get(SLEEP_GOAL)))
            }
            Config.eventBus.SPORTS_GOAL_EXERCISE_STEPS -> {
                val step: String = event.data.toString()
                // setting_step.setContentText(step)
            }
            Config.eventBus.EVENT_BUS_IMG_HEAD -> {

            }
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
            TLog.error("==" + Hawk.get<String>("address"))
            if (Hawk.get<String>("address").isNullOrEmpty()
                && reMac.isEmpty()
            ) {
                Hawk.put("address", reMac)
                TLog.error("内部==" + userInfo.user.mac)
            }
            Hawk.put("address", reMac)

            XXPermissions.with(this).permission(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION).request { permissions, all ->
                //  tvReconnection.text = resources.getString(R.string.string_conn_ing)
                moreList.forEach {
                    if(it.mac.equals(reMac, ignoreCase = true)){
                        it.connstatusEnum = ConnstatusEnum.CONNECTING
                    }
                }
                recordAdapter?.notifyDataSetChanged()
                BleConnection.initStart(Hawk.get(Config.database.DEVICE_OTA, false), 3000)
            }

        }, 1000)


    }

}