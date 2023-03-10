package com.app.fmate.ui

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.ParcelUuid
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.fmate.Config.database.DEVICE_OTA
import com.app.fmate.Config.eventBus
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.adapter.ScanAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.blecontent.BleConnection.connectDevice
import com.app.fmate.blecontent.BleConnection.iFOta
import com.app.fmate.blecontent.BleConnection.iFonConnectError
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.livedata.ScannerLiveData
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.utils.PermissionUtils
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog.Companion.error
import com.app.fmate.viewmodel.MainViewModel
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.XXPermissions
import com.ly.genjidialog.GenjiDialog
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.Config
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_ble_conne.*
import kotlinx.android.synthetic.main.title_bar_two.*
import no.nordicsemi.android.support.v18.scanner.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.*

class BleConnectActivity :
    BaseActivity<BaseViewModel>() {
    companion object {
        var connect = false
    }

    var scanner = BluetoothLeScannerCompat.getScanner()
    var mScannerLiveData: ScannerLiveData? = null
    var mScanAdapter: ScanAdapter? = null
    var viewModel: MainViewModel? = null
    private var blueAdapter =
        BluetoothAdapter.getDefaultAdapter()
    var  startScanZeroNum=0

    //???????????????????????????????????????
    private var searchName: String? = null

    //??????id ??????1?????????2
    private var categoryId = 1

    override fun layoutId(): Int {
        return R.layout.activity_ble_conne
    }

    override fun initView(savedInstanceState: Bundle?) {
        locationEnablePermission()
        SNEventBus.register(this)
        ImmersionBar.with(this)
            .titleBar(include_title)
            .init()
        viewModel = ViewModelProvider(this@BleConnectActivity)
            .get(
                MainViewModel::class.java
            )
        tv_titlebar_title.text = "????????????"
        img_calendar.setImageResource(R.mipmap.icon_arrow_left)
        img_calendar.setOnClickListener { finish() }
        dialog()
        mScannerLiveData = ScannerLiveData()


        searchName = intent.getStringExtra("scan_name")
        categoryId = intent.getIntExtra("category_id",1)

        TLog.error("-----categoryId="+categoryId)
        if(searchName != null){
            scanDescTv.text = searchName
        }

        val typeImgUrl = intent.getStringExtra("scan_img")
        if(typeImgUrl != null){
            Glide.with(this).clear(scanTypeImgView)
            Glide.with(this).load(typeImgUrl).into(scanTypeImgView)
        }


        if (!blueAdapter.isEnabled) {
            //turnOnBluetooth()
            if (blueAdapter.isEnabled) //??????????????? ??????????????????
            {
                startScan()
            }
        } else {
            startScan()
        }

        btnAdd.setOnClickListener {
            if(!turnOnBluetooth())  {
                TLog.error("????????????")
                return@setOnClickListener
            }
            try {
                startScan()
            }catch (e:Exception){
                e.printStackTrace()
            }

            TLog.error("??????+"+showOnWindow)
            showOnWindow?.showOnWindow(supportFragmentManager)
        }
        
        
        try {
            XXPermissions.with(this).permission(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).request { permissions, all ->  }
        }catch (e : Exception){
            e.printStackTrace()
        }
        
    }

    override fun createObserver() {
        super.createObserver()
            TLog.error("createObserver==")
    }

    private fun startScan() {
        val filters: MutableList<ScanFilter> =
            ArrayList()
        val mScanSettings =
            ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(false)
                .build()
        scanner.stopScan(mScanCallback)
        filters.add(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(Config.serviceUUID))
                .build()
        ) //???????????????uuid???????????????????????????id??????
        filters.add(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(Config.OTAServiceUUID))
                .build()
        )
        //????????????ota?????????uuid
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(Config.GOODX_OTA_SERVICE_UUID)).build())

//        if(searchName != null && searchName.equals("????????????")){
//            filters.add(ScanFilter.Builder().setDeviceName("StarLink Ring").build())
//            filters.add(ScanFilter.Builder().setDeviceName("starLink Ring").build())
//        }else{
//            filters.add(ScanFilter.Builder().setDeviceName("StarLink GT1").build())
//            filters.add(ScanFilter.Builder().setDeviceName("StarLink GT2").build())
//        }

        scanner.startScan(filters, mScanSettings, mScanCallback)
    }

    private val tmpScanList = mutableListOf<ScanResult>()

    private var mScanCallback: ScanCallback =
        object : ScanCallback() {
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                super.onScanResult(callbackType, result)
                TLog.error("onScanResult result++"+result.device.address)
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)
                tmpScanList.clear()
                results.forEach {
                    Log.e("OTA??????","--------????????????="+it.device.address +" "+ it.device.name+" "+Arrays.toString(it.scanRecord?.bytes)+"\n"+ it.scanRecord?.bytes?.get(4)?.toInt())
                }
                //  hideWaitDialog()
//                TLog.error("results+="+results.size)
                if(results.isEmpty())
                {
                    startScanZeroNum++
                }
                else
                {
                    startScanZeroNum=0
                }


                if(categoryId == 1){    //?????? 8008
                    results.forEach {
                        val recordArray = it.scanRecord?.bytes
                        if(recordArray != null){
                            if((recordArray[4].toInt() == -1 && recordArray[5].toInt() ==8)){
                                tmpScanList.add(it)
                            }
                        }

                    }
                }else{  //??????  8001  8003
                    results.forEach {
                        val recordArray = it.scanRecord?.bytes
                        if(recordArray != null){
                            if((recordArray[4].toInt() == -1 && recordArray[5].toInt() ==1) || (recordArray[4].toInt() == -1 && recordArray[5].toInt() ==3) || (recordArray[4].toInt() == -1 && recordArray[5].toInt() ==6)
                                || (recordArray[4].toInt() == 7 && recordArray[5].toInt() ==-86)){
                               TLog.error("--------??????llll="+it.device.name)

                                tmpScanList.add(it)
                            }
                        }

                    }
                }

                mScannerLiveData!!.onScannerResult(tmpScanList)
                if(startScanZeroNum>5)
                {
                    TLog.error("??????????????????")
                    startScanZeroNum=0
                   if(blueAdapter.isEnabled)
                       startScan()
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
//                scanner.stopScan(mScanCallback)
                TLog.error("onScanFailed errorCode++"+errorCode)
                ShowToast.showToastLong("??????????????????,???????????????????????????????????????")
                return
            }
        }

    fun setAdapter(mBleRy: RecyclerView) {
        mBleRy.layoutManager = LinearLayoutManager(
            this@BleConnectActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        TLog.error("=="+mScannerLiveData!!.getScanResultList().size)
        mScanAdapter = ScanAdapter(mScannerLiveData!!.getScanResultList())
        mBleRy.adapter = mScanAdapter
        mScanAdapter!!.setOnItemClickListener { adapter, view, position ->
            scanner.stopScan(mScanCallback)
            Hawk.put(DEVICE_OTA,false)
            iFOta = false
            iFonConnectError = false
            Hawk.put("iFonConnectError","BleConnectActivity BleConnection.iFonConnectError=false")
            connect = true
         //   BLEManager.getInstance().dataDispatcher.clearAll()
            showWaitDialog("?????????...")
            Hawk.put("OTAFile","")
            if (!blueAdapter.isEnabled) {
                ShowToast.showToastLong("???????????????,???????????????")
                hideWaitDialog()
                return@setOnItemClickListener
            }


            //?????????????????????????????????????????????????????????Mac?????????
            val savedMac = Hawk.get("address","")
            if(!TextUtils.isEmpty(savedMac)){
                BLEManager.getInstance().disconnectDevice(savedMac.toUpperCase(Locale.ROOT))
                Hawk.put("address", "")
                Hawk.put("name", "")
                BleConnection.Unbind = true
                Hawk.put("Unbind", "MyDeviceActivity Unbind=true")
            }


            connectDevice(mScanAdapter!!.data[position].device.address
            , mScanAdapter!!.data[position]?.scanRecord!!
            ,1)
        }
    }

    override fun onPause() {
        hideWaitDialog()
//        if(showOnWindow!=null)
//        showOnWindow?.dismiss()
    //    showOnWindow = null
        super.onPause()
    }


    override fun onResume() {
        super.onResume()
        TLog.error("onResume ==="+mScannerLiveData)
        mScannerLiveData?.observe(
            this,
            Observer { scannerLiveData: ScannerLiveData ->
                if (mScanAdapter != null) {
                    mScanAdapter!!.data.clear()
                    mScanAdapter!!.addData(scannerLiveData.getScanResultList())
                    //?????????????????????????????? ??????????????????????????????
                    mScanAdapter!!.notifyDataSetChanged()
                }
            }
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventContent(event: SNEvent<Any>) {
        when (event.code) {
            eventBus.DEVICE_CONNECT_HOME -> {
                ShowToast.showToastLong(getString(R.string.bind_success))
                error("HomeFragment BleConnectActivity==$connect")

                val userInfo = Hawk.get(com.app.fmate.Config.database.USER_INFO, LoginBean())

              TLog.error("--------???????????????="+Gson().toJson(userInfo)+"\n"+Hawk.get("address", ""))
//                if(userInfo != null){
//                    userInfo.user.mac =  Hawk.get("address", "")
//                    Hawk.put(com.example.xingliansdk.Config.database.USER_INFO,userInfo)
//                }

                if(baseDialog.isShowing)
                hideWaitDialog()
//                JumpUtil.startMainHomeActivity(this)

                XingLianApplication.getXingLianApplication().setCategoryId(categoryId)

                val intent = Intent()
                intent.putExtra("reback",0)
                setResult(0x00,intent)

                finish()
            }
            eventBus.DEVICE_TIME_OUT->
            {
                if(iFonConnectError)
                ShowToast.showToastLong(getString(R.string.bind_fail))
                if(baseDialog.isShowing)
                hideWaitDialog()
                startScan()
                showOnWindow?.dismiss()
            }
            eventBus.DEVICE_NOTIFY_TIME_OUT->
            {
                TLog.error("??????time ??????")
              //  if(iFonConnectError)
                ShowToast.showToastLong("????????????,????????????????????????????????????")
                if(baseDialog.isShowing)
                    hideWaitDialog()
                startScan()
                showOnWindow?.dismiss()
            }
        }
    }
    private var showOnWindow:GenjiDialog? = null

    private fun dialog() {
        showOnWindow  = newGenjiDialog {
            layoutId = R.layout.dialog_ble_scan
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_CENTER
            animStyle = R.style.BottomTransAlphaADAnimation
            convertListenerFun { holder, dialog ->
                var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                var mBleRy = holder.getView<RecyclerView>(R.id.recyclerview_ble)
                mBleRy?.let { it1 -> setAdapter(it1) }
                dialogCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.stopScan(mScanCallback)
        SNEventBus.unregister(this)
    }

    private var dialog: AlertDialog? = null
    private fun locationEnablePermission()
    {
    //????????????,  ???????????????
    if (!PermissionUtils.hasLocationEnablePermission(this)) {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
        val permissionNames = getString(R.string.content_permission_location)
        val message = SpannableStringBuilder(
            """
              ${getString(R.string.content_authorized_to_use)}
              $permissionNames
              """.trimIndent()
        )
        message.setSpan(
            ForegroundColorSpan(Color.RED),
            message.length - permissionNames.length,
            message.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        dialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(R.string.content_authorized)
            .setMessage(message)
            .setNegativeButton(getString(R.string.content_cancel), null)
            .setPositiveButton(getString(R.string.content_approve)
            ) { dialog, which -> PermissionUtils.startToLocationSetting(this@BleConnectActivity) }
            .show()
        return
    }
    }
}