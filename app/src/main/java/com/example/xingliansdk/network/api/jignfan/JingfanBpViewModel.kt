package com.example.xingliansdk.network.api.jignfan

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.jignfan.JingfanBpApi.Companion.jingfanBpApi
import com.example.xingliansdk.network.requestCustomWeight
import com.google.gson.Gson
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog

open class JingfanBpViewModel : BaseViewModel(){


    val resultJF: MutableLiveData<Any> = MutableLiveData()
    val msgJf: MutableLiveData<String> = MutableLiveData()


    val uploadJfBp : MutableLiveData<Any> = MutableLiveData()
    val msgJfUploadBp : MutableLiveData<Any> = MutableLiveData()

    //标记惊帆血压
    fun markJFBpData(value: HashMap<String, String>){
        requestCustomWeight({
            jingfanBpApi.markJFBp(value)
        },
            {resultJF.postValue(it)},
            {
            code, message ->
        message?.let {
            msgJf.postValue(it)
            TLog.error("==" + Gson().toJson(it))
            ShowToast.showToastLong(it)

        }
    })
    }

    //上传惊帆血压数据
    fun uploadJFBpData(bpArray : IntArray,time : String){
        requestCustomWeight({
            jingfanBpApi.uploadJfBp(bpArray,time)
        },
            {uploadJfBp.postValue(it)},
            {
                    code, message ->
                message?.let {
                    resultJF.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                    ShowToast.showToastLong(it)

                }
            })
    }

}