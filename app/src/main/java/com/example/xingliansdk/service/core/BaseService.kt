package com.example.xingliansdk.service.core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.xingliansdk.service.core.annotation.Works


open class BaseService : Service() {
    private  var iWorks:MutableList<IWork>  = mutableListOf()
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initWorks(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        iWorks.forEach {
            it.destroy()
        }
    }
    private fun initWorks(baseService: BaseService) {

        val annotation:Works? = baseService.javaClass.getAnnotation(Works::class.java)
        annotation?:return
        val value = annotation.value
        value.forEach { kClass ->
            val iWork:IWork = kClass.java.newInstance()
            iWorks.add(iWork)
            iWork.init(applicationContext)
        }
    }
}