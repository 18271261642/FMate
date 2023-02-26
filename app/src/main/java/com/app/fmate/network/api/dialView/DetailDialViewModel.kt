package com.app.fmate.network.api.dialView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.requestCustom
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog


class DetailDialViewModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun updateUserDial(hashMap: HashMap<String, String>) {
        requestCustom({
            DetailDialViewApi.mDetailDialViewApi.updateUserDial(hashMap)
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