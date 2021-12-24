package com.example.xingliansdk.broadcast

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.xingliansdk.Config.database
import com.example.xingliansdk.Config.eventBus
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.blecontent.BleConnection.initStart
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.utils.ResUtil.getContext
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import com.shon.connector.utils.TLog.Companion.error

class BluetoothMonitorReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (action != null) {
            TLog.error("action+=" + action[0].toShort())
            TLog.error("action+=" + Gson().toJson(action.toString()))
            TLog.error("action+=" + Gson().toJson(action))
            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        BluetoothAdapter.STATE_ON -> {
                            error("广播重连机制")
                            TLog.error("开启蓝牙")
                            if (BleConnection.iFonConnectError)
                                initStart(Hawk.get(database.DEVICE_OTA, false), 60000)
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            BleConnection.iFonConnectError = true
                            //断开链接的回调
                            SNEventBus.sendEvent(eventBus.DEVICE_DISCONNECT)
                            Toast.makeText(context, "蓝牙已经关闭", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                //    SNEventBus.sendEvent(eventBus.DEVICE_DISCONNECT)
                    error("====设备断开链接")
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    error("====链接成功")
                }
                BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED -> {
                    error("====请求断开链接")
                }
            }
        }
    }
}