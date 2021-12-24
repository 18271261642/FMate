package com.example.xingliansdk.ui

import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.shon.connector.utils.TLog
import com.shon.bluetooth.core.call.Listener
import com.shon.bluetooth.util.ByteUtil

class Ble5Activity : BaseActivity<BaseViewModel>() {
    var address=""

    override fun layoutId()=R.layout.activity_ble5

    override fun initView(savedInstanceState: Bundle?) {
        address=intent.getStringExtra("address").toString()
        if(address.isNotEmpty())
            listener5(address)
//        button.setOnClickListener {
//            TLog.error("address+=$address")
//            WriteCall(address)
//                .setServiceUUid(XingLianApplication.serviceUUID5)
//             .setCharacteristicUUID(XingLianApplication.writeCharacter5)
//                .enqueue(TestWrite5Call(address))
//
//        }
    }

    private fun listener5(address: String?) {
        Listener(address)
            .enqueue { address, result,_ ->
                TLog.error(
                    "address++$address\n第二个数据==" + ByteUtil.getHexString(
                        result
                    )
                )

                true
            }
    }
}