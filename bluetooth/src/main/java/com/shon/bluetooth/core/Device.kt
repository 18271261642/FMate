package com.shon.bluetooth.core

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import com.shon.connector.utils.TLog
import com.shon.bluetooth.util.BleLog
import com.shon.connector.Config

/**
 * Auth : xiao.yunfei
 * Date : 2020/10/06 20:21
 * Package name : com.shon.bluetooth.contorller.bean
 * Des : 已连接的设备 管理
 *
 */

class ConnectedDevices {
    private val devices: MutableList<Device> = mutableListOf()

    fun getDevice(address: String): Device? {
        return devices.find {
            it.deviceAddress.equals(address,ignoreCase = true) //兼容大小写
        }
    }

    fun onDeviceConnectError(address: String, status: Int) {
        TLog.error("onDeviceConnectError getDevice(address) ?"+getDevice(address))
        if(getDevice(address)==null) {
            TLog.error("走event了")
         //   SNEventBus.sendEvent(133)
        return
        }
        getDevice(address)?.let {
            it.connect?.connectCallback?.onConnectError(address, status)
            TLog.error("onDeviceConnectError 没删除?")
            devices.remove(it)
        }
    }

    fun onDeviceDisConnect(bluetoothDevice: BluetoothDevice, gatt: BluetoothGatt) {
        val address = bluetoothDevice.address
        Config.isNeedTimeOut = false
        TLog.error("onDeviceDisConnect  ++$address")
        getDevice(address)?.let {
            BleLog.d("设备断开连接，回调给已存在列表中的设备")
            it.connect?.connectCallback?.onDisconnected(address)
            TLog.error("onDeviceDisConnect 没删除?")
            devices.remove(it)
        }
    }

    fun onServicesDiscovered(address: String) {
        getDevice(address)?.let {

            BleLog.d("准备回调给用户 设备 服务已开启")
            it.connect?.connectCallback?.onServiceEnable(address, it.gatt)
        }
    }

    fun addDevice(device: Device) {
        devices.add(device)
    }

}

/**
 * 已连接的设备
 */
data class Device(
    var deviceName: String? = null,
    var deviceAddress: String? = null,
    var serviceEnable: Boolean = false,  //服务 是否被 启用
    var connected: Boolean = false,  //连接状态
    var gatt: BluetoothGatt? = null,  //
    var connect: Connect? = null  //
)