package com.example.xingliansdk.network.api.mapView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustom
import com.shon.connector.utils.ShowToast

class MapMotionViewModel : BaseViewModel() {
    val result: MutableLiveData<Any> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getMotionDistance(type:Int) {
        requestCustom({
            MapMotionViewApi.mMapMotionViewApi.getMotionDistance(type)
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
}