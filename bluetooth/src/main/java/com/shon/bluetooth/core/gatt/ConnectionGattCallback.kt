package com.shon.bluetooth.core.gatt

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import com.shon.bluetooth.ConnectDispatcher
import com.shon.bluetooth.util.BleLog

/**
 * Auth : xiao.yunfei
 * Date : 2020/9/28 16:23
 * Package name : com.shon.bluetooth.core.gatt
 * Des :
 */
class ConnectionGattCallback(private val connectDispatcher: ConnectDispatcher) {

//   private lateinit var  bleGatt: BluetoothGatt
    fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        BleLog.d(" onConnectionStateChange  status = $status ; newState = $newState")
//        bleGatt= gatt
        val device = gatt.device

        BleLog.d("  device name  = " + device.name + " ; mac = " + device.address)
        //这里是处理 133的情况但是不太确定
//        if(newState== BluetoothGatt.GATT_SUCCESS&&status==133)
//        {
//            //直接重连  反正暂时知道的情况是 oppo在上一步重连的时候回 133错误 不能重连成功,并且 connect为null所以只能此处在针对所以
//            //设备为133的情况都进行重连但不查找设备是否存在
//            BleLog.d("  device 133特殊事件")
//            refreshDeviceCache(gatt)
//            gatt.close()
//          //  refreshDeviceCache()
//           // connectDispatcher.onDeviceError(device.address,status)
//
//            connectDispatcher.onDeviceConnected(device, gatt)
//            return
//        }

        if (status != BluetoothGatt.GATT_SUCCESS){
       //     ConnectDispatcher.refreshCache(gatt)
            gatt.close()
         //   refreshDeviceCache()
            connectDispatcher.onDeviceError(device.address,status)
            return
        }
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
//                gatt.discoverServices()
                connectDispatcher.onDeviceConnected(device, gatt)
            }
            BluetoothProfile.STATE_DISCONNECTED -> {
//                ConnectDispatcher.refreshCache(gatt)
                gatt.close()
             //   refreshDeviceCache()
                connectDispatcher.onDeviceDisConnected(device, gatt)
            }
            BluetoothProfile.STATE_CONNECTING -> {
            }
        }

    }


    fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return
        }
        val device = gatt.device
        connectDispatcher.onServicesDiscovered(device.address)

    }
    /**
     * 清除蓝牙缓存
     */
    private fun refreshDeviceCache(gatt: BluetoothGatt): Boolean {
        gatt.let {
            try {
                val localMethod = it.javaClass.getMethod("refresh")
                if (localMethod != null) {
                    return (localMethod.invoke(it) as Boolean)
                }
            } catch (localException: Exception) {
                localException.printStackTrace()
            }
        }
        return false
    }
}