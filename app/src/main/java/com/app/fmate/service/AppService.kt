package com.app.fmate.service

import android.content.Intent
import com.app.fmate.R
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.service.core.BaseService
import com.app.fmate.service.core.annotation.Works
import com.app.fmate.service.work.BleWork
import com.app.fmate.service.work.WeatherWork
import com.app.fmate.utils.AppDataNotifyUtil

@Works([BleWork::class,WeatherWork::class])
open class AppService: BaseService() {

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