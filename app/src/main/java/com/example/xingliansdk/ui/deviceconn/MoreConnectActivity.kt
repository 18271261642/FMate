package com.example.xingliansdk.ui.deviceconn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.moreDevice.ConnectRecordViewModel
import com.example.xingliansdk.utils.GsonUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_blood_pressure.titleBar
import kotlinx.android.synthetic.main.activity_more_connect_layout.*
import me.hgj.jetpackmvvm.ext.util.toJson
import org.json.JSONObject
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

        recordAdapter!!.setOnMoreConnDeleteListener {
            wearDialog(0,moreList.get(it).mac)
        }

    }



    private fun initData(){
        mViewModel.getConnRecordDevice()

    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.recordDeviceResult.observe(this){
            TLog.error("-------data="+it.list.toString())



//            val jsonObject = JSONObject(it.toJson())
//
//            val listStr = jsonObject.get("data")

//            val recordListBean = GsonUtils.getGsonObject<ConnRecordListBean>(it.toJson())

//

//            val lt = GsonUtils.getGsonObject<List<ConnectedDeviceBean>>(Gson().toJson(listStr))
//
            moreList.clear()
            if (it.list!= null) {
                moreList.addAll(it.list)
            }
            recordAdapter?.notifyDataSetChanged()

        }

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
                                BLEManager.getInstance().disconnectDevice(mac.toUpperCase(Locale.ROOT))
                                // BLEManager.getInstance().dataDispatcher.clear(Hawk.get("address"))
                                BLEManager.getInstance().dataDispatcher.clearAll()

                                Hawk.put("ELECTRICITY_STATUS", -1)

                                Handler(Looper.getMainLooper()).postDelayed({
                                    val value = HashMap<String, String>()
                                    value["mac"] = ""
                                    if(moreList.size == 0){
                                        mViewModel.setUserInfo(value)
                                    }
                                   // mViewModel.setUserInfo(value)
                                    mViewModel.deleteRecordByMac(mac.toLowerCase(Locale.ROOT))

                                    Hawk.put("address", "")
                                    Hawk.put("name", "")
                                    BleConnection.Unbind = true
                                    Hawk.put("Unbind", "MyDeviceActivity Unbind=true")
                                    SNEventBus.sendEvent(Config.eventBus.DEVICE_DELETE_DEVICE)
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


}