package com.app.fmate.network.api.mapView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.requestCustom
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