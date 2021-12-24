package com.example.xingliansdk.utils

import com.shon.bluetooth.core.call.Listener

object BleUtil {
    fun ownListener(address: String?) {
        Listener(address)
            .enqueue { address, result,_ ->

                false
            }
    }

    fun bigListener(address: String?) {
        Listener(address)
            .enqueue { address, result,_ ->
                false
            }
    }
}