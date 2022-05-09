package com.example.xingliansdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.IntentFilter
import android.os.*
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.adapter.ScanAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.broadcast.BleBroadcastReceiver
import com.example.xingliansdk.dfu.DFUActivity
import com.example.xingliansdk.ui.Ble5Activity
import com.example.xingliansdk.ui.TemperatureActivity
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.shon.connector.utils.TLog.Companion.error
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.core.Connect
import com.shon.bluetooth.core.ConnectCallback
import com.shon.bluetooth.core.call.Listener
import com.shon.bluetooth.core.call.NotifyCall
import com.shon.bluetooth.core.call.WriteCall
import com.shon.bluetooth.core.callback.NotifyCallback
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.DataBean
import com.shon.connector.bean.PushBean
import com.shon.connector.bean.TimeBean
import com.shon.connector.call.write.controlclass.ReminderPushCall
import com.shon.connector.call.write.settingclass.TimeCall
import com.shon.connector.utils.HexDump
import kotlinx.android.synthetic.main.activity_main.*
import no.nordicsemi.android.support.v18.scanner.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent as Intent1


class MainActivity : BaseActivity<BaseViewModel>(),
    View.OnClickListener,
    BleWrite.FirmwareInformationInterface/*,BleBroadcastReceiver.MessageInfo*/ {
    var scanner = BluetoothLeScannerCompat.getScanner()
    lateinit var mScanAdapter: ScanAdapter
    lateinit var mList: ArrayList<ScanResult>
    private lateinit var address: String
    private var intentFilter: IntentFilter? = null
    private var mBleBroadcastReceiver: BleBroadcastReceiver? = null
    val ACTION = "bleReceiver"
    var name: String = ""
    var mBean: DataBean =
        DataBean()

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String) {
        this.address = address
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_scan -> {
                val blueAdapter = BluetoothAdapter.getDefaultAdapter()
                if (!blueAdapter.isEnabled) {
                    //turnOnBluetooth()
                } else {
                    showWaitDialog()
                    stopScan()

                    val scanner = BluetoothLeScannerCompat.getScanner()
                    val settings: ScanSettings = ScanSettings.Builder()
                        .setLegacy(false)
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setReportDelay(3000)
                        .setUseHardwareBatchingIfSupported(false)
                        .build()
                    val filters: MutableList<ScanFilter> = ArrayList()
                    filters.add(
                        ScanFilter.Builder().build()
                    )

                    scanner.startScan(filters, settings, mScanCallback)

//
//                    val settings: ScanSettings = ScanSettings.Builder()
//                        .setLegacy(false)
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                        .setReportDelay(3000)
//                        .setUseHardwareBatchingIfSupported(true)
//                        .build()
//                    val filters: MutableList<ScanFilter> = ArrayList()
//                    filters.add(ScanFilter.Builder().build())
//               //  filters.add(ScanFilter.Builder().setDeviceAddress(ParcelUuid.fromString(XingLianApplication.serviceUUID)).build())//应该是加入uuid的话只能搜索到指定id内容
//                    scanner.startScan(filters, settings, mScanCallback)
                }

            }
            R.id.btn_write -> {
                //做一个写的操作 发送
                // sendData()
                if (Hawk.get<String>("address").isNullOrEmpty()) {
                    ShowToast.showToastLong("请连接蓝牙")
                    return
                }
                var key = 0x00
                if (btn_write.text == "打开温度") {
                    btn_write.text = "关闭温度"
                    key = 0x02
                } else if (btn_write.text == "关闭温度") {
                    btn_write.text = "打开温度"
                    key = 0x01
                }
                0x03+0xe8
                BleWrite.writeTemperatureSwitchCall(
                    key.toByte(),
                    BleWrite.TemperatureSwitchCallInterface { })
                startActivity(android.content.Intent(this, TemperatureActivity::class.java))
            }
            R.id.btn_disconnect -> {//断开链接

                setData =
                    HexDump.byteMerger(setData, keyData) //如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF

                error("%==${ByteUtil.getHexString(setData)}")
                if (Hawk.get<String>("address").isNullOrEmpty()) {
                    ShowToast.showToastLong("蓝牙未连接")
                    return
                }
                BLEManager.getInstance().disconnectDevice(address)
            }
            R.id.btn_time -> {//设置时间
                setTime()

            }
            R.id.btn_red -> //写入
            {
                mBean.sleep = mBean.sleep + 10
                TLog.error("getSleep+=" + mBean.sleep)

                val test = byteArrayOf(0x43)
                val fen = test[0].toInt() and 0x0f
                val mu = (test[0].toInt() shr 4) and 0x0f
                TLog.error("===${fen}")
                TLog.error("mu===${mu}")
//                BleWrite.writeForGetFirmwareInformation( BleWrite.NoticeInterface {
//                        productNumber, version, nowMaC, mac ->
//                }
//                )
            }
            R.id.btn_rsc -> {
                startActivity(Intent1(this, MainActivity2::class.java))
            }
            R.id.btn_six_one -> {
                sendFirmwareInformation()
//                WriteCall(address)
//                    .setServiceUUid(XingLianApplication.serviceUUID1)
//                    .setCharacteristicUUID(XingLianApplication.mWriteCharactertest)
//                    .enqueue(
//                        SetTimeWriteCall(
//                            address
//                        )
//                    )
            }

        }
    }

    private fun setTime() {
        var year = "00"
        var mother = "00"
        var day = "00"
        var hours = "00"
        var min = "00"
        var ss = "00"
        if (edt_year.text.toString().isNotEmpty())
            year = edt_year.text.toString()
        if (edt_mother.text.toString().isNotEmpty())
            mother = edt_mother.text.toString()
        if (edt_day.text.toString().isNotEmpty())
            day = edt_day.text.toString()
        if (edt_hours.text.toString().isNotEmpty())
            hours = edt_hours.text.toString()
        if (edt_min.text.toString().isNotEmpty())
            min = edt_min.text.toString()
        if (edt_ss.text.toString().isNotEmpty())
            ss = edt_ss.text.toString()
//        val mSetTimeBean = SettingTimeBean()
//        mSetTimeBean.setYear(year)
//        mSetTimeBean.setMonth(mother)
//        mSetTimeBean.setDay(day)
//        mSetTimeBean.setHours(hours)
//        mSetTimeBean.setMin(min)
//        mSetTimeBean.setSs(ss)
//        val data = ByteArray(7)
//        error("year.substring+=" + HexDump.hexStringToBytes(year))
//        TLog.error("mother+=" + HexDump.hexStringToBytes(mother))
//        TLog.error("year.substring(0,2).toInt()+=" + year.substring(0, 2).toInt())
//        TLog.error("10转16==" + Integer.toHexString(year.substring(0, 2).toInt()))
//        TLog.error("==" + HexDump.hexStringToBytes(year.substring(0, 2)).size)
//        TLog.error("==" + HexDump.hexStringToBytes(year.substring(0, 2))[0])
//        System.arraycopy(HexDump.hexStringToBytes(year.substring(0, 2)), 0, data, 0, 1)
//        System.arraycopy(HexDump.hexStringToBytes(year.substring(2, year.length)), 0, data, 1, 1)
//        System.arraycopy(HexDump.hexStringToBytes(mother), 0, data, 2, 1)//从第三位开始,后面year要拆成2份
//        System.arraycopy(HexDump.hexStringToBytes(day), 0, data, 3, 1)
//        System.arraycopy(HexDump.hexStringToBytes(hours), 0, data, 4, 1)
//        System.arraycopy(HexDump.hexStringToBytes(min), 0, data, 5, 1)
//        System.arraycopy(HexDump.hexStringToBytes(ss), 0, data, 6, 1)
//        sendData2(mSetTimeBean)
        val mTimeBean = TimeBean()
        mTimeBean.characteristic = 1//闹钟
        mTimeBean.number = 0//编号
        mTimeBean.switch = 2//打开开关
        mTimeBean.specifiedTime = 12//是否只执行一次并且是周几执行,bit字节代表bit0=星期日--bit6=星期六,bit7=1时为执行一次0时表示每次都会响
        mTimeBean.year = year.toInt()
        mTimeBean.month=mother.toInt()
        mTimeBean.day=day.toInt()
        mTimeBean.hours = hours.toInt()
        mTimeBean.min = min.toInt()
        mTimeBean.unicode = "今天天气不错下午要出差去搞点事情"
        mTimeBean.unicodeType = 49
        sendAlarmClockSchedule(mTimeBean)
    }

    override fun onPause() {
        super.onPause()
        stopScan()
    }

    fun stopScan() {
        scanner.stopScan(mScanCallback)
    }

    //    lateinit var newList: ArrayList<ScanResult>
    var mScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            val builder = StringBuilder()
            for (i in results.indices) {
                builder.append(results[i].toString())
                builder.append("\n")
            }
            rv_scan.visibility = View.VISIBLE
            TLog.error("onBatchScanResults$builder")
            TLog.error("扫描到的数据长度++${results.size}")
//            mList.clear()
            results.forEach {
                if (!it.device.name.isNullOrEmpty()) {
                    TLog.error("it.device.name==" + it.device.name)
                    mList.add(it)
                }

                //   mList.distinct()
                //newList=  mList.takeIf {child->!child.isNullOrEmpty() }?.distinctBy {it.device.address}as ArrayList<ScanResult>
            }

            Handler(Looper.getMainLooper()).post() {
                TLog.error("执行了该操作" + mList.size)
                hideWaitDialog()
                mScanAdapter.addData(mList)
                //   mScanAdapter.setNewData(mList)
                //扫描完 暂停扫描 不知道放这里对不对
                stopScan()
                //   mScanAdapter.replaceData(newList)
                //     TLog.error("执行了该操作"+newList.size)

            }

        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    fun setAdapter() {
        rv_scan.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mList = ArrayList()
        mScanAdapter = ScanAdapter(mList)
        // rv_scan.addItemDecoration(DividerItemDecoration(this, MyDecoration.VERTICAL_LIST))
        rv_scan.adapter = mScanAdapter
        mScanAdapter.setOnItemClickListener { _, _, position ->
            // setAddress(mScanAdapter.data[position].device.address)
            address = mScanAdapter.data[position].device.address
            TLog.error("点击获取的数据++${Gson().toJson(mScanAdapter.data[position])}")
            TLog.error("点击获取的数据++${Gson().toJson(mScanAdapter.data[position].device)}")
            TLog.error("点击获取的数据++${Gson().toJson(mScanAdapter.data[position].device.bondState)}")
            //getAddress()?.let {  connectDevice(it)}
            connectDevice(address) //点击链接设备
        }
    }

    private fun connectDevice(address: String) {
        Connect(address)
            .setTimeout(10_000)
            .setReTryTimes(1)
            .enqueue(object : ConnectCallback() {
                override fun onConnectSuccess(address: String?, gatt: BluetoothGatt?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        gatt?.readPhy()
                        gatt?.setPreferredPhy(BluetoothDevice.PHY_LE_2M_MASK,BluetoothDevice.PHY_LE_2M_MASK,BluetoothDevice.PHY_OPTION_S2)
                    }
                    gatt?.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                }

                override fun onConnectError(address: String?, errorCode: Int) {
                    TLog.error("链接错误=$errorCode")
                }

                override fun onServiceEnable(address: String?, gatt: BluetoothGatt?) {
                    error("gatt=${gatt?.let { it.device.name }}")

                    ShowToast.showToastLong("连接成功")
//                    Hawk.put("address", address)
                    name = gatt?.let { it.device.name }.toString()
//                    Hawk.put("name", name)
                    rv_scan.visibility = View.GONE
                    when {
                        address.equals("F5:85:66:C1:CC:A0") -> {
                            notify5call()
                        }
                        address.equals("D5:CE:62:A9:10:89") -> {
                            TLog.error("来了来了")
                            //SdkNotifycall()
                             x61Notifycall()

                        }
                        address.equals("D5:CE:62:A9:10:8A") -> {//先通过 x61把设备 变为ota模式 再次连接的时候再进入DFUTestActivity
                            startActivity(
                                Intent1(this@MainActivity, DFUActivity::class.java)
                                    .putExtra("address", address)
                                    .putExtra("name", name)
                            )
                        }
                        else -> {
                            TLog.error("开通至")
                                SdkNotifycall()
//                            val intent =
//                                Intent1(ACTION)
//                            intent.putExtra("address", address)
//                            sendBroadcast(intent) // 发送广播
                        }
                    }
                }

                override fun onDisconnected(address: String?) {
                }

            })
    }

    fun SdkNotifycall() {
        TLog.error("开通至2")
        NotifyCall(address)
            .setServiceUUid(XingLianApplication.serviceUUID)
            .setCharacteristicUUID(XingLianApplication.readCharacter)
            .enqueue(
                object : NotifyCallback() {
                    override fun getTargetSate(): Boolean {
                        return true
                    }

                    override fun onTimeout() {
                        TLog.error("onTimeout")
                    }

                    override fun onChangeResult(result: Boolean) {
                        super.onChangeResult(result)
                        TLog.error("开通至222++$result")
                        if (result) {

                            x61Listener(address)
                        }
                    }
                })
    }

    fun x61Notifycall() {
        NotifyCall(address)
            .setCharacteristicUUID(XingLianApplication.readCharactertest)
            .setServiceUUid(XingLianApplication.serviceUUID1)
            .enqueue(
                object : NotifyCallback() {
                    override fun getTargetSate(): Boolean {
                        return true
                    }

                    override fun onTimeout() {}
                    override fun onChangeResult(result: Boolean) {
                        super.onChangeResult(result)
                        if (result) {
                            x61Listener(address)
                        }
                    }
                })
    }

    fun x61Listener(address: String?) {
        Listener(address)
            .enqueue { address, result,uuid ->
                error(
                    "address++$address\n第二个数据==" + ByteUtil.getHexString(
                        result
                    )
                )

                true
            }
    }

    fun notify5call() {
        NotifyCall(address)
            .setServiceUUid(XingLianApplication.serviceUUID5)
            .setCharacteristicUUID(XingLianApplication.readCharacter5)
            .enqueue(
                object : NotifyCallback() {
                    override fun getTargetSate(): Boolean {
                        return true
                    }

                    override fun onTimeout() {}
                    override fun onChangeResult(result: Boolean) {
                        super.onChangeResult(result)
                        if (result) {
                            startActivity(
                                android.content.Intent(
                                    this@MainActivity,
                                    Ble5Activity::class.java
                                ).putExtra("address", address)
                            )
                            //   listener5(address)
                        }
                    }
                })
    }

//    fun sendData() {
//        WriteCall(address)
//            .setServiceUUid(XingLianApplication.serviceUUID1)
//            .setCharacteristicUUID(XingLianApplication.mWriteCharactertest)
//            .enqueue(TestWriteCall(address))
//
//    }

    //    fun send()
//    {
//        WriteCall(address)
//            .setServiceUUid(XingLianApplication.serviceUUID1)
//            .setCharacteristicUUID(XingLianApplication.mWriteCharactertest)
//            .enqueue(DeviceFirmwareCall(address,this))
//
//    }
    fun sendData2(data: TimeBean) {


        WriteCall(address)
            .setServiceUUid(XingLianApplication.serviceUUID1)
            .setCharacteristicUUID(XingLianApplication.mWriteCharactertest)
            .enqueue(
                TimeCall(
                    address,
                    data
                )
            )
    }

    fun sendFirmwareInformation() {
        BleWrite.writeForGetFirmwareInformation(this,false) //重新发送的操作
    }

    fun sendAlarmClockSchedule(mTimeBean: TimeBean) {
        BleWrite.writeAlarmClockScheduleCall(mTimeBean,false)
    }

    fun sendPush(data: PushBean) {


        for (i in 0..1) {
            WriteCall(address)
                .setServiceUUid(XingLianApplication.serviceUUID1)
                .setCharacteristicUUID(XingLianApplication.mWriteCharactertest)
                .enqueue(
                    ReminderPushCall(
                        address,
                        data,
                        i
                    )
                )
        }
    }

    fun listener() {
        Listener(address)
            .enqueue { _, result,_ ->
                TLog.error("获取温度++${ByteUtil.getHexString(result)}")
                if (result.size == 20) {
                    var humidity = result[19].toInt() + (result[18].toInt() shl 8)  //湿度
                    var temperature = result[17].toInt() + (result[16].toInt() shl 8) //温度
                    TLog.error("humidity+=${humidity}\ntemperature+=${temperature}")
                    TLog.error("humidity+=${result[17].toInt()}\ntemperature+=${result[16].toInt() shl 8}")
                    var diastolicBloodPressure = result[15].toInt()  //舒张压  范围40-250
                    var systolicBloodPressure = result[14].toInt()//收缩压   范围40-250
                    var bloodOxygen = result[13].toInt() //血氧  0-100
                    var heartRate = result[12].toInt() //心率 40-250
                    //   if (humidity >= Config.HUMIDITY_MAX)

                    if (temperature >= Config.TEMPERATURE_MAX)
                        temperature -= Config.TEMPERATURE_MAX
                    if (diastolicBloodPressure == 255) {
                        diastolicBloodPressure = -1
                    }
                    if (systolicBloodPressure == 255) {
                        systolicBloodPressure = -1
                    }
                    if (bloodOxygen == 255) {
                        bloodOxygen = -1
                    }
                    if (heartRate == 255) {
                        heartRate = -1
                    }
                    Handler(Looper.getMainLooper()).post() {
                        var num1: BigDecimal =
                            BigDecimal(temperature).divide(BigDecimal(10))//精确值的算法
                        var num2: BigDecimal = BigDecimal(humidity).divide(BigDecimal(10))//精确值的算法
                        tv_motion.text =
                            "心率: ${heartRate}" +
                                    "\n血氧: ${bloodOxygen}" +
                                    "\n舒张压: ${systolicBloodPressure}" +
                                    "\n收缩压: ${diastolicBloodPressure}" +
                                    "\n温度: ${num1}" +
                                    "\n湿度: ${num2}"
                    }
                }
                if (result.size == 17) {
                    result[2].toInt() shl 16
                    val num: Int =
                        ((result[3].toInt() shl 16) + (result[4].toInt() shl 12) + (result[5].toInt() shl 8)) + result[6].toInt()
                    TLog.error("步数++${ByteUtil.getHexString(result).substring(6, 14).toInt(16)}")
                    TLog.error("步数++${num}")
                    TLog.error("result[2]++" + (result[3].toInt() shl 16))
                    TLog.error("result[2]++" + (result[4].toInt() shl 12))
                    TLog.error("result[2]++" + (result[5].toInt() shl 8))
                    TLog.error("result[7]++" + result[6].toInt())
                    TLog.error("总和++${((result[3].toInt() shl 16) + (result[4].toInt() shl 12) + (result[5].toInt() shl 8)) + result[6].toInt()}")
                    TLog.error("距离++${ByteUtil.getHexString(result).substring(14, 22).toInt(16)}")
                    TLog.error("卡路里++${ByteUtil.getHexString(result).substring(22, 30).toInt(16)}")
                    TLog.error("时间++${ByteUtil.getHexString(result).substring(30, 34).toInt(16)}")
                    Handler(Looper.getMainLooper()).post() {
                        tv_motion.text =
                            "步数: ${ByteUtil.getHexString(result).substring(6, 14).toInt(16)}" +
                                    "\n距离: ${ByteUtil.getHexString(result).substring(14, 22)
                                        .toInt(16)}" +
                                    "\n卡路里: ${ByteUtil.getHexString(result).substring(22, 30)
                                        .toInt(16)}" +
                                    "\n时间: ${ByteUtil.getHexString(result).substring(30, 34)
                                        .toInt(16)}"
                    }


                }
                true
            }
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    var setData: ByteArray = byteArrayOf()
    var run=40
    override fun initView(savedInstanceState: Bundle?) {
        Permissions()
        btn_scan.setOnClickListener(this)
        btn_write.setOnClickListener(this)
        btn_disconnect.setOnClickListener(this)
        btn_time.setOnClickListener(this)
        btn_red.setOnClickListener(this)
        btn_rsc.setOnClickListener(this)
        btn_six_one.setOnClickListener(this)
        setAdapter()
//        intReceiver()
        testByte()
        TLog.error("时间戳==" + System.currentTimeMillis())
        val time = (System.currentTimeMillis() / 1000)
        val key = java.lang.Long.toHexString((System.currentTimeMillis() / 1000))
        val one: Int = key.substring(0, 2).toInt(16)
        val two: Int = key.substring(2, 4).toInt(16)
        val three: Int = key.substring(4, 6).toInt(16)
        val four: Int = key.substring(6, 8).toInt(16)
        TLog.error("第一个值=${one}")
        TLog.error("第一个值=${two}")
        TLog.error("第一个值=${three}")
        TLog.error("第一个值=${four}")

        TLog.error("keyTime=${HexDump.toHexString(one)}")
        TLog.error("key+=${key}")

        CircularProgressView.setOnClickListener {
            val msg = Message()
            handler.sendMessage(msg)

        }
//        TLog.error("keyTime${keyTime[0]}")
//        TLog.error("keyTime=${ByteUtil.getHexString(keyTime)}")

    }
    private var type = 1 //页面类型

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (type) {
                1 -> {
                    val rand = Random()
                    CircularProgressView.setTextSize(60)
                    CircularProgressView.setColor(R.color.color_text_blue)
                    CircularProgressView.setText(2000)
                    CircularProgressView.setProgress(rand.nextInt(100),1000)  //这个成功了 其他几个都没改变颜色 字体大小以及显示值
                }
            }
        }
    }

    val keyData = byteArrayOf(
        0x88.toByte(),
        0x00,
        0x01,
        0x00,
        0x00,
        0x00,
        0x0C,
        0x40,
        0x03,
        0x02,
        0x00,
        0x06,
        0xFF.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0X81.toByte(),
        0X01,
        0X01,
        0XC6.toByte(),
        0X01,
        0XC6.toByte()
    )

    private fun testByte() {
        //11-28日新协议
        var key = 0x00
        for (i in 8 until keyData.size) { //要求key=a^b^c
//            key = key xor +keyData[i]
            //要求key=a^b^c
            key = key xor keyData[i].toInt()
//            TLog.error("keyData[i].toInt()==${keyData[i].toInt()}===${i}")
//            TLog.error("keyData[i].toInt()==${HexDump.toHexString(keyData[i])}")
        }
        val type = ((0XFF) shl 5) + 0xFF
        val mTwo = (0XFF shr 5)
        val monday=0
        val tuesday=0
        val wednesday=1
        val thursday=0
        val friday=1
        val saturday=0
        val sunday=0
        error("周几+${HexDump.toHexString((sunday+(monday shl 1)+(tuesday shl 2)+(wednesday shl 3)+(thursday shl 4)+(friday shl 5)+(saturday shl 6)).toByte()
        )}")
        TLog.error("type=${type}")
        TLog.error("最终+=${HexDump.toHexString(key)}")
//        val sixteen = "A6"
//        val ten = 0XFF.toInt()
        val two = Integer.toBinaryString(0XFF)
        TLog.error("最终+=${HexDump.getXocInt(0XFF.toByte()) + 0XFF}")
        error("16===${two}")
        TLog.error("mTwo=${mTwo}")
        TLog.error("mTwo+=${HexDump.toHexString(mTwo)}")
    }

    override fun onResult(
        productNumber: String?,
        versionName: String?,
        version: Int,
        nowMaC: String?,
        mac: String?
    ) {

    }


//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(mBleBroadcastReceiver)
//    }


//    override fun getMessage(data: DataBean) {
//        TLog.error("返回数据++${Gson().toJson(data)}")
//        tv_motion.text =
//            "心率: ${data.heartRate}" +
//                    "\n血氧: ${data.bloodOxygen}" +
//                    "\n舒张压: ${data.systolicBloodPressure}" +
//                    "\n收缩压: ${data.diastolicBloodPressure}" +
//                    "\n温度: ${data.temperature}" +
//                    "\n湿度: ${data.humidity}"
//    }

}