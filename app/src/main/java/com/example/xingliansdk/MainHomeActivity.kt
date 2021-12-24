package com.example.xingliansdk

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.activity.OnBackPressedCallback
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.broadcast.BluetoothMonitorReceiver
import com.example.xingliansdk.network.manager.NetState
import com.example.xingliansdk.service.AppService
import com.example.xingliansdk.service.SNAccessibilityService
import com.example.xingliansdk.utils.AppActivityManager
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.PermissionUtils
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.viewmodel.MainViewModel
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import kotlin.system.exitProcess


class MainHomeActivity : BaseActivity<MainViewModel>() {
    var exitTime = 0L
    var bleListener:BluetoothMonitorReceiver?=null

    var bleListener1: LocalBroadcastManager? = null
    var bluetoothMonitorReceiver: BluetoothMonitorReceiver? = null
    override fun layoutId() = R.layout.activity_main_home

    override fun initView(savedInstanceState: Bundle?) {
        Permissions()

        bindBle()
        mainViewModel.userInfo()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainHomeActivity, R.id.host_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.mainFragment) {
                    nav.navigateUp()
                } else {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ShowToast.showToastLong("再按一次退出程序")
                        exitTime = System.currentTimeMillis()
                    } else {
                        Hawk.put("iFonConnectError","MainHomeActivity BleConnection.iFonConnectError=true")
                        BleConnection.iFonConnectError=true
                        if(Hawk.get<String>("address").isNotEmpty()) {
                            BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                            BLEManager.getInstance().dataDispatcher.clearAll()
                        }
                        val intent = Intent(this@MainHomeActivity, AppService::class.java)
                        stopService(intent)
                        finish()
                      //  exitProcess(0)
                    }
                }
            }
        })
    }

    override fun createObserver() {
        super.createObserver()


    }
    /**
     * 某些手机会杀掉下面的服务
     */
    private fun restartServiceIfNeed() {

        if (!HelpUtil.isServiceRunning(this, AppService::class.java)) {
            Hawk.put("isServiceStatus","MainHomeActivity BleConnection.isServiceStatus =true")
            BleConnection.isServiceStatus =true
            val intent = Intent(this, AppService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }



    override fun onResume() {
        super.onResume()
//        TLog.error("MainHomeActivity  onResume+"+ BLEManager.isConnected)
        if (BleConnection.iFonConnectError) {
            TLog.error("没连接的时候重连")
            BleConnection.initStart(Hawk.get(Config.database.DEVICE_OTA, false))
        }
        initPermission2()
        restartServiceIfNeed()
    }

    private var dialog: AlertDialog? = null
    /**
     * 初始化推送服务监听
     */
    private fun initPermission2() {
        //通知监听权限
        //TODO 这里如果重复判断权限==false 可能需要延迟0.5~1秒再判断, 因为系统数据库插入开关值是一个子线程操作, 回到该界面马上调用提供者 有可能获取到的还是之前的开关状态
        val hasNotificationPermission: Boolean =
            PermissionUtils.hasNotificationListenPermission(this)
        val isAccessibilityServiceRunning: Boolean = PermissionUtils.isServiceRunning(
            this,
            SNAccessibilityService::class.java
        )
        //TODO 如果没有通知权限,同时辅助服务没有运行  才提示需要授权,  否则 如果辅助服务在运行,通知服务没运行, 那就先用辅助服务顶替.
        //TODO 注意 这里判断的是两种通知监听服务,勿混淆,  逻辑是 通知服务无效则使用辅助服务,通知服务和辅助服务都有效,则优先使用通知服务作为消息推送主要数据来源
        if (!hasNotificationPermission && !isAccessibilityServiceRunning) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val permissionNames = getString(R.string.content_permission_notification)
            val message =
                SpannableStringBuilder(
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
                .setPositiveButton(
                    getString(R.string.content_approve)
                ) { dialog, _ -> PermissionUtils.startToNotificationListenSetting(this@MainHomeActivity) }
                .show()
            return
        }
        //请求重新绑定 通知服务,防止未开启
        PermissionUtils.requestRebindNotificationListenerService(this)

        //位置权限,  蓝牙扫描用
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
                ) { dialog, which -> PermissionUtils.startToLocationSetting(this@MainHomeActivity) }
                .show()
            return
        }



        if (!PermissionUtils.hasNotificationEnablePermission(this)) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val permissionNames =
                getString(R.string.content_permission_notification_enable)
            val message =
                SpannableStringBuilder(
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
                .setPositiveButton(
                    getString(R.string.content_approve)
                ) { dialog, which ->
                    PermissionUtils.startToNotificationEnableSetting(
                        this@MainHomeActivity,
                        null
                    )
                }.show()
            return
        }


    }

    private fun bindBle()
    {
        bleListener= BluetoothMonitorReceiver()
        val intentFilter = IntentFilter()
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        // 注册广播
        registerReceiver(bleListener, intentFilter);

//        bleListener1 = LocalBroadcastManager.getInstance(this)
//        bluetoothMonitorReceiver = BluetoothMonitorReceiver()
//        val intentFilter1 = IntentFilter()
//        // 监视蓝牙设备与APP连接的状态
//        intentFilter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//        intentFilter1.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
//        // 注册广播
//        bleListener1?.registerReceiver(bluetoothMonitorReceiver!!, intentFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bleListener)
        bleListener1?.unregisterReceiver(bluetoothMonitorReceiver!!)
    }
}