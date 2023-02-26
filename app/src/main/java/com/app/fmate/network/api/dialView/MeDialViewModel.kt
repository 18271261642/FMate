package com.app.fmate.network.api.dialView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.requestCustom
import com.shon.connector.utils.ShowToast
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