package com.example.xingliansdk.network.api.javaMapView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.homeView.HomeViewApi
import com.example.xingliansdk.network.requestCustom
import com.example.xingliansdk.utils.ShowToast

open class MapViewModel : BaseViewModel() {
    val result: MutableLiveData<MapVoBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun motionInfoGetList(date: HashMap<String, Any>) {
        requestCustom({
            HomeViewApi.mHomeViewApi.motionInfoGetList(date)
        }, {
            result.postValue(it)
        }) { code, message ->
            message?.let {
                ShowToast.showToastLong(it)
                msg.postValue(it)
            }
        }
    }


}
