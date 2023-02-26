package com.app.fmate.service.core

import android.content.Context
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

interface IWork {


    fun init(context:Context) {
        SNEventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventMessage(eMessage: SNEvent<*>){
        onReceivedMessage(eMessage)
    }


    fun onReceivedMessage(eMessage: SNEvent<*>)
    fun destroy(){
        SNEventBus.unregister(this)
    }
}