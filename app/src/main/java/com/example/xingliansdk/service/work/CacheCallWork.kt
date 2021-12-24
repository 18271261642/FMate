package com.example.xingliansdk.service.work

import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.service.core.IWork
import com.shon.bluetooth.core.call.ICall
import com.shon.bluetooth.core.call.WriteCall

/**
 * 缓存
 */
class CacheCallWork :IWork {

    val cacheCalls :MutableList<ICall<*>> = mutableListOf()
    override fun onReceivedMessage(eMessage: SNEvent<*>) {

        //新增消息
        addWork(eMessage)

        //设备连接的消息
        onDeviceConnected(eMessage)
    }

    private fun onDeviceConnected(eMessage: SNEvent<*>) {
       val  address = eMessage.data as String
        cacheCalls.forEach {
            if (it is WriteCall){
                it.enqueue(it.callBack)
            }


        }
    }

    private fun addWork(eMessage: SNEvent<*>) {
        val iCall: ICall<*> = eMessage.data as ICall<*>
        cacheCalls.add(iCall)
    }
}