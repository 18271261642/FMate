package com.example.xingliansdk.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.broadcast.BleBroadcastReceiver
import com.shon.connector.utils.ShowToast
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_temperature.*

class TemperatureActivity : BaseActivity<BaseViewModel>() {
    private var intentFilter: IntentFilter? = null
    private var mBleBroadcastReceiver: BleBroadcastReceiver? = null
    val ACTION = "bleReceiver"
    override fun layoutId(): Int = R.layout.activity_temperature
    override fun initView(savedInstanceState: Bundle?) {
        intReceiver()
        val intent =
            Intent(ACTION)
        intent.putExtra("address",Hawk.get<String>("address"))
        sendBroadcast(intent)
        btn_open.setOnClickListener {
            if ( Hawk.get<String>("address").isNullOrEmpty()) {
                ShowToast.showToastLong("请链接蓝牙")
                return@setOnClickListener
            }
            var key = 0x00
            if (btn_open.text == "打开温度") {
                btn_open.text = "关闭温度"
                key = 0x02
            } else if (btn_write.text == "关闭温度") {
                btn_write.text = "打开温度"
                key = 0x01
            }
            BleWrite.writeTemperatureSwitchCall(
                key.toByte(),
                BleWrite.TemperatureSwitchCallInterface { })
        }
    }
    private fun intReceiver() {
        intentFilter = IntentFilter()
        intentFilter?.addAction(ACTION)
        mBleBroadcastReceiver = BleBroadcastReceiver()
        registerReceiver(mBleBroadcastReceiver, intentFilter)
    }
//    override fun getMessage(data: DataBean) {
//        TLog.error("返回数据++${Gson().toJson(data)}")
//        tv_humidity.text="当前湿度:  "+data.humidity
//        tv_temperature.text="当前温度:  "+data.temperature
//    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBleBroadcastReceiver)
    }
}