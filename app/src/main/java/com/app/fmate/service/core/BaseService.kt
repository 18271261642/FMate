package com.app.fmate.service.core

import android.content.Intent
import android.os.IBinder
import com.app.fmate.service.core.annotation.Works
import com.app.fmate.service.work.BleWork
import com.hjq.http.lifecycle.LifecycleService


open class BaseService : LifecycleService() {

    private var privateIWork : BleWork ?= null

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

    public fun getWorks(){

    }
}