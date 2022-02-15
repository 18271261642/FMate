package com.shon.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.shon.connector.utils.TLog
import com.shon.bluetooth.BLEManager.isConnected
import com.shon.bluetooth.core.Connect
import com.shon.bluetooth.core.ConnectedDevices
import com.shon.bluetooth.core.Device
import com.shon.bluetooth.util.BleLog
import java.util.*

/**
 * Auth : xiao.yunfei
 * Date : 2020/10/06 20:19
 * Package name : com.shon.bluetooth
 * Des :  设备连接分发器
 */
class ConnectDispatcher internal constructor() {
    val connectedDevices: ConnectedDevices = ConnectedDevices()
    private val connectList: ArrayList<Connect> = ArrayList()
    private val handler: Handler = Handler(Looper.getMainLooper())

    /**
     * 设备连接异常
     *
     * @param address
     * @param status
     */
    fun onDeviceError(address: String?, status: Int) {
        stopTime()
        if (connectedDevices.getDevice(address!!) != null) {
            handlerConnectError(connectedDevices.getDevice(address!!)!!.connect, address, status)
        } else {
            handlerConnectError(connect, address, status)
        }
    }

    private fun handlerConnectError(connect: Connect?, address: String?, status: Int) {
        connect ?: return
        if (!TextUtils.equals(address, connect!!.address)) return
        TLog.error("onDeviceError address $address  connect!!.address  ${connect!!.address}")
        if (connect.reTryTimes <= 0) {
            TLog.error("onDeviceError connect!!.reTryTimes <= 0")
            handler.post {
                TLog.error("onDeviceError handler ")
                connect?.connectCallback?.onConnectError(address, status)
                startNextConnect(true)
            }
            return
        }
        connect!!.reTryTimes = connect!!.reTryTimes - 1
        handler.post {
            connect.connectCallback?.onConnectError(address, status)
            TLog.error("address++"+address)
            TLog.error("status++"+status)
            this.connect=connect
            connectDevice()
        }

    }

    /**
     * 设备断开连接
     *
     * @param bluetoothDevice
     * @param gatt
     */
    fun onDeviceDisConnected(bluetoothDevice: BluetoothDevice?, gatt: BluetoothGatt) {
        BleLog.d("onDeviceDisConnected  ")
        stopTime()
        handler.post { connectedDevices.onDeviceDisConnect(bluetoothDevice!!, gatt) }
        connect?.let {
            handler.post {
                BleLog.d("设备断开连接，回调给正在连接中的设备")
                it.connectCallback?.onDisconnected(bluetoothDevice?.address)
                connectList.remove(it)
                startNextConnect(true)
            }
        }
        connectList.find {
            TextUtils.equals(it.address, bluetoothDevice!!.address)
        }?.let {
            handler.post {
                BleLog.d("设备断开连接，回调给等待连接中的设备")
                it.connectCallback?.onDisconnected(bluetoothDevice?.address)
                connectList.remove(it)
            }
        }

    }

    /**
     * 设备连接成功
     *
     * @param bluetoothDevice
     * @param gatt
     */
    fun onDeviceConnected(bluetoothDevice: BluetoothDevice?, gatt: BluetoothGatt) {
        stopTime()
        val address = bluetoothDevice!!.address
        TLog.error("address==$address")
        var device = connectedDevices.getDevice(address)

        TLog.error("device==$device")
        if (device != null) {
            TLog.error("非null==")
            device.connected = true
            isConnected = true
            device.gatt = gatt  //重置 gatt 对象,
            handler.post { device!!.connect!!.connectCallback?.onConnectSuccess(address, gatt) }
            startNextConnect(true)
            gatt.discoverServices()
            return
        }
        BleLog.d("已连接设备列表中 不存在该设备信息，准备添加信息")
        BleLog.d("connect==$connect   address==$address    connect?.address==${connect?.address}")
        if (connect != null && TextUtils.equals(address, connect?.address)) {
            handler.post { connect?.connectCallback?.onConnectSuccess(address, gatt) }
            device = Device()
            device.gatt = gatt
            device.connect = connect
            device.deviceAddress = address
            device.connected = true
            isConnected = true
            device.deviceName = bluetoothDevice.name
            connectedDevices.addDevice(device)
            BleLog.d("连接设备列表添加信息成功")
            gatt.discoverServices()
        }
        BleLog.d("----> startNextConnect")
        handler.postDelayed({
            startNextConnect(true)
        }, 200)

    }

    /**
     * 设备服务被 启用
     *
     * @param address
     */
    fun onServicesDiscovered(address: String?) {
        handler.post { connectedDevices.onServicesDiscovered(address!!) }
    }

    fun enqueue(connect: Connect) {
        connectList.add(0, connect)
        startNextConnect(false)
    }

    private var connect: Connect? = null

    @Synchronized
    private fun startNextConnect(finish: Boolean) {
        if (finish) {
//            TLog.error("finish=$finish")
            if (connect != null) {
                connectList.remove(connect!!)
                connect = null
            }
        }
//        TLog.error("connect++$connect")
        if (connect != null || connectList.isEmpty()) return
        //    TLog.error("connect++$connect")
//        TLog.error(" connectList[connectList.size - 1]++${ connectList[connectList.size - 1]}")
        connect = connectList[connectList.size - 1]
        connectDevice()
    }

    private fun connectDevice() {
        TLog.error("connect+"+connect)
        TLog.error("connect?.address+"+connect?.address)
        val device = connect?.address?.let { connectedDevices?.getDevice(it) }
        if (device != null && device.connected) {
            BleLog.d("该设备已连接")
            connect!!.connectCallback.onConnected()
            startNextConnect(true)
        } else {
            BleLog.d("开始新的设备连接 ： " + connect!!.address)
            val adapter = BLEManager.getInstance().manager.adapter ?: return
            val remoteDevice = adapter.getRemoteDevice(connect!!.address)
            val context = BLEManager.getInstance().context
//            TLog.error("context=$context  remoteDevice=$remoteDevice")
            if (context == null || remoteDevice == null) {
                return
            }
            connect!!.connect(context, remoteDevice, BLEManager.getInstance().gattCallback)
            startTimer(connect!!)
        }
    }

    /**
     * 断开链接
     */

    @Synchronized
    fun disconnect(mac: String?) {
        BLEManager.getInstance().dataDispatcher.clear(mac)
        connectedDevices.getDevice(mac!!)?.let { device ->
            TLog.error("getDevice 不为null")
            //如果不加这句话好像就没办法用重连 但是加了嗯 ota升级又有问题
            device.gatt?.let { gatt ->
                TLog.error("gatt 不为null")

                refreshCache(gatt)
                gatt.disconnect()
                gatt.close()
            }
            device.connected = false
            isConnected = false
            //这句话先注释放这里 也是我自己添加的
            // device = Device()
        }
    }

    /**
     * 断开链接
     */

    @Synchronized
    fun unbind(mac: String?) {
        connectedDevices.getDevice(mac!!)?.let { device ->
            TLog.error("getDevice 不为null")
            //如果不加这句话好像就没办法用重连 但是加了嗯 ota升级又有问题
            device.gatt?.let { gatt ->
                TLog.error("gatt 不为null")
                gatt.disconnect()
                refreshCache(gatt)
                gatt.close()
            }
            device.connected = false
            isConnected = false
        }

    }

    private fun refreshCache(gatt: BluetoothGatt) {
        try {
            val localMethod = gatt.javaClass.getMethod("refresh") ?: return
            localMethod.invoke(gatt)
        } catch (var3: Exception) {
            BleLog.e("An exception occured while refreshing device")
        }
    }

    private fun startTimer(connect: Connect) {
        handler.postDelayed(timeoutRunnable, connect.timeOut)
    }

    private val timeoutRunnable = Runnable {
        onTimeout(connect?.address)
    }

    private fun stopTime() {
        handler.removeCallbacks(timeoutRunnable)
    }

    private fun onTimeout(address: String?) {

        connect ?: return
        TLog.error("onTimeout  ++$address, reTryTimes = ${connect!!.reTryTimes}")
        if (!TextUtils.equals(address, connect!!.address)) return
        if (connect!!.reTryTimes <= 0) {
            handler.post {
                connect?.connectCallback?.onTimeout()
                startNextConnect(true)
            }
            return
        }
        connect!!.reTryTimes = connect!!.reTryTimes - 1
        connectDevice()
    }
}