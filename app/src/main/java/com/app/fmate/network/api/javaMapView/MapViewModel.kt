package com.app.fmate.network.api.javaMapView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.api.homeView.HomeViewApi
import com.app.fmate.network.requestCustom
import com.shon.connector.utils.ShowToast

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
