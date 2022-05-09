package com.example.xingliansdk.network.api.meView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.dialView.RecommendDialViewApi
import com.example.xingliansdk.network.api.meView.MeViewApi.Companion.mMeViewApi
import com.example.xingliansdk.network.requestCustom
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog


class MeViewModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getDialImg(pNumber : String ) {
        requestCustom({
            mMeViewApi.getDialImg(pNumber)
        }, {
            result.postValue(it)
        }
        ) { code, message ->
            message?.let {
                msg.postValue(it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val resultDialImg: MutableLiveData<RecommendDialBean> = MutableLiveData()
    val msgDialImg: MutableLiveData<String> = MutableLiveData()
    fun findDialImg(hashMap: HashMap<String,String>) {
        requestCustom({
            RecommendDialViewApi.mRecommendDialViewApi.findDial(hashMap)
        }, {
            resultDialImg.postValue(it)
        }
        ) { code, message ->
            message?.let {
                msgDialImg.postValue(it)
                TLog.error("it=" + it)
                ShowToast.showToastLong(it)
            }
        }
    }

    val resultCheckDial: MutableLiveData<Any> = MutableLiveData()
    val msgCheckDial: MutableLiveData<String> = MutableLiveData()
   fun checkDialSate(data:String)
   {
       requestCustom({
           RecommendDialViewApi.mRecommendDialViewApi.checkDialSate(data)
       }, {
           resultCheckDial.postValue(it)
       }
       ) { code, message ->
           message?.let {
               msgCheckDial.postValue(it)
               ShowToast.showToastLong(it)
           }
       }
   }
}