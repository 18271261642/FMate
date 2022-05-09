package com.example.xingliansdk.network.api.dialView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustom
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog


class RecommendDialViewModel : BaseViewModel() {
    val result: MutableLiveData<RecommendDialBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun findDialImg(hashMap: HashMap<String,String>) {
        requestCustom({
            RecommendDialViewApi.mRecommendDialViewApi.findDial(hashMap)
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
}