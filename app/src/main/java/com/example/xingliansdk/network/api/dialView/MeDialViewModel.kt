package com.example.xingliansdk.network.api.dialView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog


class MeDialViewModel : BaseViewModel() {
    val result: MutableLiveData<DownDialModel> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun findMyDial() {
        requestCustom({
            MeDialViewApi.mMeDialViewApi.findMyDial()
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val result1: MutableLiveData<RecommendDialBean> = MutableLiveData()
    fun findDialImg(hashMap: HashMap<String,String>) {
        requestCustom({
            RecommendDialViewApi.mRecommendDialViewApi.findDial(hashMap)
        }, {
            result1.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val resultUpdate: MutableLiveData<Any> = MutableLiveData()
    fun updateUserDial(hashMap: HashMap<String,String>) {
        requestCustom({
            DetailDialViewApi.mDetailDialViewApi.updateUserDial(hashMap)
        }, {
            resultUpdate.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val resultDeleteMyDial: MutableLiveData<Any> = MutableLiveData()
    fun deleteMyDial(id:String) {
        requestCustom({
            MeDialViewApi.mMeDialViewApi.deleteMyDial(id)
        }, {
            resultDeleteMyDial.postValue(it)
        }
        ) { code, message ->
            message?.let {
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }
}