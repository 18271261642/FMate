package com.example.xingliansdk.network.api.bloodPressureView

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustomBig
import com.example.xingliansdk.network.requestCustomWeight
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureApi.Companion.bloodPressureApi
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.shon.connector.utils.TLog

open class BloodPressureViewModel : BaseViewModel() {
    val resultSet: MutableLiveData<Any> = MutableLiveData()
    val msgSet: MutableLiveData<String> = MutableLiveData()
    val msgGet: MutableLiveData<String> = MutableLiveData()
    val resultGet: MutableLiveData<BloodPressureVoBean> = MutableLiveData()
    val resultDelete: MutableLiveData<Any> = MutableLiveData()
    val msgDelete: MutableLiveData<String> = MutableLiveData()
    fun getBloodPressure(  date: String) {
        requestCustomBig({
            bloodPressureApi.getBloodPressure(date)
        }, {
            resultGet.postValue(it)
        }, { code, message ->

            message?.let {
                msgGet.postValue(it)
            }
        })
    }

    fun setBloodPressure(context: Context,createTime: Long,systolicPressure : Int,diastolicPressure:Int) {
        requestCustomWeight(
            {
                bloodPressureApi.saveBloodPressure( createTime,systolicPressure,diastolicPressure)
            }, {
                TLog.error("==" + Gson().toJson(it))
                resultSet.postValue(it)
            },
            { code, message ->
                message?.let {
                    msgSet.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                   if(HelpUtil.netWorkCheck(context))
                    ShowToast.showToastLong(it)
                    else
                       ShowToast.showToastLong("请开启网络,进行上传")
                }
            })
    }
    fun deleteBloodPressure( value: HashMap<String, String>) {
        requestCustomWeight(
            {
                bloodPressureApi.deleteBloodPressure(value)
            }, {
                TLog.error("==" + Gson().toJson(it))
                resultDelete.postValue(it)
            },
            { code, message ->
                message?.let {
                    msgDelete.postValue(it)
                    TLog.error("==" + Gson().toJson(it))
                    ShowToast.showToastLong(it)

                }
            })
    }

}
