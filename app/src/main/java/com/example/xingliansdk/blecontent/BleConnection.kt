package com.example.xingliansdk.blecontent
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import com.example.xingliansdk.Config.database
import com.example.xingliansdk.Config.database.DEVICE_OTA
import com.example.xingliansdk.Config.eventBus.*
import com.example.xingliansdk.XingLianApplication.Companion.mXingLianApplication
import com.example.xingliansdk.bean.MessageBean
import com.example.xingliansdk.broadcast.BleBroadcastReceiver
import com.example.xingliansdk.dfu.DFUActivity
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.ui.BleConnectActivity
import com.example.xingliansdk.utils.BleUtil
import com.example.xingliansdk.utils.RoomUtils
import com.example.xingliansdk.utils.ShowToast
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.core.Connect
import com.shon.bluetooth.core.ConnectCallback
import com.shon.bluetooth.core.call.NotifyCall
import com.shon.bluetooth.core.callback.NotifyCallback
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.utils.TLog
import no.nordicsemi.android.support.v18.scanner.*
import java.util.*

@SuppressLint("StaticFieldLeak")
object BleConnection {

    var isServiceStatus = false
    //ota 设置
    var iFOta: Boolean = Hawk.get(DEVICE_OTA, false)
    //链接错误,也就是 意外断开
   public var iFonConnectError = true
    //解绑
    var Unbind = false
    var startOTAActivity=true
    fun connectDevice(/*mContext: FragmentActivity,*/ address: String, scanRecord: ScanRecord,type:Int) {
        timer?.cancel()
        stopScanner()
        Connect(address)
            .setTimeout(10000)
            .enqueue(object : ConnectCallback() {
                override fun onConnectSuccess(
                    address: String,
                    gatt: BluetoothGatt
                ) {
                    TLog.error("链接++onConnectSuccess")
                    stopScanner()
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        gatt?.readPhy()
//                        gatt?.setPreferredPhy(
//                            BluetoothDevice.PHY_LE_2M_MASK,
//                            BluetoothDevice.PHY_LE_2M_MASK,
//                            BluetoothDevice.PHY_OPTION_S2
//                        )
//                    }
//                    gatt?.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                    Hawk.put<ArrayList<MessageBean>>(
                        database.MESSAGE_CALL,
                        ArrayList()
                    )
                    var name = gatt.device.name
                    val mList: List<ParcelUuid> = scanRecord.serviceUuids!!
                    val manufacturerSpecificData = scanRecord?.manufacturerSpecificData?.keyAt(0)
                    for (i in mList.indices) {
//                        TLog.error(" mList[i].uuid.toString()=="+ mList[i].uuid.toString())
                        if (Config.OTAServiceUUID.equals(
                                mList[i].uuid.toString(),
                                ignoreCase = true
                            ) &&
                            (manufacturerSpecificData == 32769 || manufacturerSpecificData == 65535)
                        ) //ota模式到这里即可 非OTA才打开通知
                        {
                            TLog.error("进入ota")
                            iFonConnectError = false
                            Hawk.put("iFonConnectError","BleConnection BleConnection.iFonConnectError=false")
                            SNEventBus.sendEvent(DEVICE_OTA_UPDATE)
                            if (!startOTAActivity) //不用跳转不用下方操作 ,有个页面是先进入ota在断开
                                return
                            val intent = Intent()
                            Hawk.put(DEVICE_OTA, true)
                            intent.setClass(mXingLianApplication, DFUActivity::class.java)
                            intent.putExtra("address", address)
                            intent.putExtra("name", name)
                            intent.putExtra("productNumber", manufacturerSpecificData.toString(16))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            mXingLianApplication.getContext()?.startActivity(intent)
                            return
                        }
                    }

                    //    SNEventBus.sendEvent(DEVICE_CONNECT_NOTIFY) //个人建议还是放在service和通知那里 因为就算连接上也存在 service链接不上  133问题
                }

                override fun onTimeout() {
                    TLog.error("onTimeout")
                    if (Hawk.get(DEVICE_OTA, false)) //ota过了
                    {
                        ShowToast.showToastLong("升级链接超时")
                        return
                    }
                    BLEManager.getInstance().disconnectDevice(address)
                    SNEventBus.sendEvent(DEVICE_TIME_OUT)
                    //   ShowToast.showToastLong("链接超时")
                }

                override fun onConnectError(address: String, errorCode: Int) {
                    if (Hawk.get(DEVICE_OTA, false)) //ota过了
                    {
                        return
                    }
                    SNEventBus.sendEvent(DEVICE_TIME_OUT)
                    if (Unbind) //解绑情况下直接返回其他操作就别管了
                    {
                        return
                    }

                    BLEManager.getInstance().disconnectDevice(address)
                    iFonConnectError = true
                    Hawk.put("iFonConnectError","BleConnection BleConnection.iFonConnectError=true")
                    //断开链接的回调
                    TLog.error("connectDevice 的  onConnectError 断开的")
                    SNEventBus.sendEvent(DEVICE_DISCONNECT)
//                    Handler(Looper.getMainLooper()).postDelayed({
                    TLog.error("开始重连找设备")
                    initStart(iFOta)
//                    }, 200)
                }

                override fun onServiceEnable(
                    address: String,
                    gatt: BluetoothGatt
                ) {
                    if (gatt.device == null || gatt.device?.name == null)
                        return
                    //                   TLog.error("???什么情况")
                    var name = gatt.device.name
                    if (name.isNullOrEmpty())
                        name = "获取硬件名字为空的测试"
                    startNotify(address, name,type)
                }

                override fun onDisconnected(address: String) {
                    TLog.error("onDisconnected   $address")
                    ShowToast.showToastLong("连接异常，请重新连接")
                    if (Unbind) //解绑情况下直接返回其他操作就别管了
                    {
                        return
                    }
                    if (Hawk.get(DEVICE_OTA, false)) //ota过了
                    {
                        TLog.error("==="+Hawk.get(DEVICE_OTA, false))
                        return
                    }
                    BLEManager.getInstance().disconnectDevice(address)
                    iFonConnectError = true
                    Hawk.put("iFonConnectError","BleConnection BleConnection.iFonConnectError=true")
                    //断开链接的回调
                    TLog.error("connectDevice 的  onDisconnected 断开的")
                    SNEventBus.sendEvent(DEVICE_DISCONNECT)
                    Handler(Looper.getMainLooper()).postDelayed({
                        initStart(/*mContext,*/ iFOta)
                    }, 200)
                }
            })
    }

    private fun startNotify(address: String, name: String,type: Int) {
        //开启 小数据的广播特征
        NotifyCall(address)
            .setCharacteristicUUID(Config.readCharacter)
            .setServiceUUid(Config.serviceUUID)
            .enqueue(object : NotifyCallback() {
                override fun getTargetSate(): Boolean {
                    return true
                }

                override fun onTimeout() {
                    TLog.error("startNotify onTimeout")
                    if(type==1)
                    {
                        SNEventBus.sendEvent(DEVICE_NOTIFY_TIME_OUT)
                    }
                }

                override fun onChangeResult(result: Boolean) {
                    super.onChangeResult(result)
                    TLog.error("startNotify onChangeResult==$result")
                    if (result) {
                        BleUtil.ownListener(address)
                    }
                }
            })
        //大数据
        NotifyCall(address)
            .setCharacteristicUUID(Config.readCharacterBig)
            .setServiceUUid(Config.serviceUUID)
            .enqueue(object : NotifyCallback() {

                override fun getTargetSate(): Boolean {
                    return true
                }

                override fun onTimeout() {
                    TLog.error("startNotify 大数据 onTimeout")
                }

                override fun onChangeResult(result: Boolean) {
                    super.onChangeResult(result)
                    TLog.error("onChangeResult==$result")
                    if (result) {
                        iFonConnectError = false
                        Hawk.put("iFonConnectError","BleConnection BleConnection.iFonConnectError=false")
                        Unbind = false
                        Hawk.put("Unbind","BleConnection Unbind=false")
                        Hawk.put("address", address)
                        BleWrite.address = address
                        SNEventBus.sendEvent(DEVICE_CONNECT_HOME)
                        timer?.cancel()
                        if (BleConnectActivity.connect) {
                            TLog.error("没进吗")
                            BleConnectActivity.connect = false
                            SNEventBus.sendEvent(DEVICE_CONNECT_HOME)
                        }
                        Hawk.put("name", name)
                        SNEventBus.sendEvent(DEVICE_CONNECT_NOTIFY)
                        //打开监听
                        BleUtil.bigListener(address)
                        //打开广播接收监听
                        intReceiver()
                        val intent =
                            Intent(BLE_ACTION)
                        intent.putExtra("address", address)
                        mXingLianApplication.sendBroadcast(intent)
                    }
                }
            })
    }

    var scanner = BluetoothLeScannerCompat.getScanner()
    private  var timer: CountDownTimer?=null
    private fun startScan() {
//        TLog.error("扫描设备")
        scanner.stopScan(mScanCallback)
        val mScanSettings =
            ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(5000)
                .setUseHardwareBatchingIfSupported(false)
                .build()
        val filters: MutableList<ScanFilter> =
            ArrayList()
            filters.add(
                ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString(Config.serviceUUID))
                    .build()
            )
            filters.add(
                ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString(Config.OTAServiceUUID))
                    .build()
            )
        scanner.startScan(filters, mScanSettings, mScanCallback)
    }
    private fun stopScanner() {
        scanner.stopScan(mScanCallback)
    }
    var mScanCallback: ScanCallback =
        object : ScanCallback() {

            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)
                results.forEachIndexed { index, it ->
//                        TLog.error("查找到的设备++" + it.device.address + "本地设备++" + Hawk.get<String>("address"))
                    if (it.device.address.equals(Hawk.get<String>("address"),ignoreCase = true)||
                            (iFOta && it.device.address.equals(Hawk.get<String>("dfuAddress"),ignoreCase = true))
                    ) {
                        TLog.error("搜索到相同设备并连接=+"+it.device.address+"=="+Hawk.get<String>("address"))
                        var address=Hawk.get("address","")
                        if(it.device.address.isNullOrEmpty()||it.device.name.isNullOrEmpty()) //测试神经操作开关蓝牙导致有蓝牙的时候突然关闭会突然为null
                            return
                        timer?.cancel()
                        scanner.stopScan(this)
                        if (it.device.address.equals(address,ignoreCase = true) &&it.device.name.equals("StarLink GT1")) {
                        //    RoomUtils.roomDeleteAll()
                        //    SNEventBus.sendEvent(HOME_CARD)
                            Hawk.put(DEVICE_OTA, false)  //如果是 address进来的说明就不是ota升级要把ota的true变为false
                          //  Hawk.deleteAll()
                        }
                        connectDevice(/*mContext,*/ it.device.address, results[index].scanRecord!!,0)
                    }
                }
            }

        }
    /**
     * isOta =true 为 是ota升级模式 false 则不是
     */
    fun initStart(isOta: Boolean, time: Long = 60000) {
        TLog.error("initStart 开始"+  Hawk.get(DEVICE_OTA, false))
        val blueAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!blueAdapter.isEnabled) {
            return
        }
        iFOta = isOta
//        TLog.error("isota==$isOta")
        val time = time
//        TLog.error("开始时间戳+" + System.currentTimeMillis())
        timer?.cancel()
        timer = null
        timer = object : CountDownTimer(time * 5, time) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                iFonConnectError = true
                Hawk.put("iFonConnectError","BleConnection BleConnection.iFonConnectError=true")
                TLog.error("initStart 的  onFinish 断开的"+Hawk.get("address", ""))
                if(Hawk.get("address", "").isNotEmpty()) {
                    TLog.error("断开链接"+Hawk.get("address", ""))
                    BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                    BLEManager.getInstance().dataDispatcher.clearAll()
                }
             //   ShowToast.showToastLong("无法扫描到该设备,请检查设备")
                SNEventBus.sendEvent(DEVICE_DISCONNECT)
                scanner.stopScan(mScanCallback)
            }
        }
        timer?.start()
        startScan()
    }

    private var intentFilter: IntentFilter? = null
    private var mBleBroadcastReceiver: BleBroadcastReceiver? = null
    private const val BLE_ACTION = "bleReceiver"
    private fun intReceiver() {
        intentFilter = IntentFilter()
        intentFilter?.addAction(BLE_ACTION)
        mBleBroadcastReceiver = BleBroadcastReceiver()
        mXingLianApplication.registerReceiver(mBleBroadcastReceiver, intentFilter)
    }
}