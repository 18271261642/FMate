package com.example.xingliansdk.service

import android.content.Intent
import com.example.xingliansdk.R
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.service.core.BaseService
import com.example.xingliansdk.service.core.annotation.Works
import com.example.xingliansdk.service.work.BleWork
import com.example.xingliansdk.service.work.WeatherWork
import com.example.xingliansdk.utils.AppDataNotifyUtil
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.TLog

@Works([BleWork::class,WeatherWork::class])
class AppService: BaseService() {

    override fun onCreate() {
//        TLog.error("启动")
        if(!BleConnection.isServiceStatus) {
            stopService(Intent(this, AppService::class.java))
//            TLog.error("服务是否启动++" + HelpUtil.isServiceRunning(this, AppService::class.java))
        }
        super.onCreate()
        AppDataNotifyUtil.updateNotificationTitle(
            this, getString(R.string.app_name), "0 步数"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
//        TLog.error("onDestroy  销毁")
    }
}