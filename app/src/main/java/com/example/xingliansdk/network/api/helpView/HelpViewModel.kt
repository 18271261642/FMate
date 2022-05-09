package com.example.xingliansdk.network.api.helpView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustom
import com.shon.connector.utils.ShowToast

open class HelpViewModel : BaseViewModel() {
    val resultHelp: MutableLiveData<Any> = MutableLiveData()
    val msgHelp: MutableLiveData<String> = MutableLiveData()
    fun saveFeedback(date: HashMap<String, Any>) {
        requestCustom({
            HelpApi.mHelpApi.saveFeedback(date)
        }, {
            resultHelp.postValue(it)
        }) { code, message ->
            message?.let {
                ShowToast.showToastLong(it)
                msgHelp.postValue(it)
            }
        }
    }


}
