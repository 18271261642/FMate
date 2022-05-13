package com.example.xingliansdk.network.api.jignfan

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.jignfan.JingfanBpApi.Companion.jingfanBpApi
import com.example.xingliansdk.network.requestCustomWeight
import com.google.gson.Gson
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog

open class JingfanBpViewModel : BaseViewModel() {


    val resultJF: MutableLiveData<Any> = MutableLiveData()
    val msgJf: MutableLiveData<String> = MutableLiveData()


    val uploadJfBp: MutableLiveData<Any> = MutableLiveData()
    val msgJfUploadBp: MutableLiveData<Any> = MutableLiveData()

    //标记惊帆血压
//    fun markJFBpData(
//        data1: String, data2: String, data3: String, sbp1: Int, sbp2: Int, sbp3: Int,
//        dbp1: Int, dbp2: Int, dbp3: Int
//    ) {
//        requestCustomWeight({
//
//            jingfanBpApi.markJFBp(data1, data2, data3, sbp1, sbp2, sbp3, dbp1, dbp2, dbp3)
//        },
//            { resultJF.postValue(it) },
//            { code, message ->
//                message?.let {
//                    msgJf.postValue(it)
//                    TLog.error("==" + Gson().toJson(it))
//                    ShowToast.showToastLong(it)
//
//                }
//            })
//    }


    //标记惊帆血压
    fun markJFBpData(
        value : HashMap<String,Any>
    ) {
        requestCustomWeight({

            jingfanBpApi.markJFBp(value)
        },
            { resultJF.postValue(it) },
            { code, message ->
                message?.let {
                    msgJf.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                    ShowToast.showToastLong(it)

                }
            })
    }



    //上传惊帆血压数据
    fun uploadJFBpData(bpArray: String, time: String) {
        requestCustomWeight({
            jingfanBpApi.uploadJfBp(bpArray, time)
        },
            { uploadJfBp.postValue(it) },
            { code, message ->
                message?.let {
                    msgJfUploadBp.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                    ShowToast.showToastLong(it)

                }
            })
    }

}