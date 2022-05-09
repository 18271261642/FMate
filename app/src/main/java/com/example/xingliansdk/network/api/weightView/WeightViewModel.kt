package com.example.xingliansdk.network.api.weightView

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustomBig
import com.example.xingliansdk.network.requestCustomWeight
import com.example.xingliansdk.bean.UpdateWeight
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.shon.connector.utils.TLog

open class WeightViewModel : BaseViewModel() {
    val result: MutableLiveData<WeightModeBean> = MutableLiveData()
    val msgGetWeight: MutableLiveData<String> = MutableLiveData()
    val msgSetWeight: MutableLiveData<String> = MutableLiveData()
    val msgDelete: MutableLiveData<String> = MutableLiveData()
    val resultSetWeight: MutableLiveData<UpdateWeight> = MutableLiveData()
    val resultDeleteWeight: MutableLiveData<Any> = MutableLiveData()
    fun getWeight(type: String, date: String) {
        requestCustomBig({
            WeightApi.weightApi.getWeight(type, date)
        }, {
            result.postValue(it)
        }, { code, message ->

            message?.let {
                msgGetWeight.postValue(it)
            }
        })
    }

    fun setWeight(context: Context, value: String) {
        requestCustomWeight(
            {
                WeightApi.weightApi.setWeight(value)
            }, {
                TLog.error("==" + Gson().toJson(it))
                resultSetWeight.postValue(it)
            },
            { code, message ->
                message?.let {
                    msgSetWeight.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                   if(HelpUtil.netWorkCheck(context))
                    ShowToast.showToastLong(it)
                    else
                       ShowToast.showToastLong("请开启网络,进行上传")
                }
            })
    }

    fun deleteWeight(context: Context,value: HashMap<String, String>) {
        requestCustomWeight(
            {
                WeightApi.weightApi.deleteWeight(value)
            }, {
                TLog.error("==" + Gson().toJson(it))
                resultDeleteWeight.postValue(it)
            },
            { code, message ->
                message?.let {
                    msgDelete.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                    if(HelpUtil.netWorkCheck(context))
                        ShowToast.showToastLong(it)
                    else
                        ShowToast.showToastLong("请开启网络,进行删除")
                }
            })
    }
}
