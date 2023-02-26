package com.app.fmate.network.api.device

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.requestCustom


//获取设备属性
class DeviceCategoryViewModel : BaseViewModel() {


    val deviceCategoryResult : MutableLiveData<DeviceCategoryBean> = MutableLiveData()
    val dCategoryMsg : MutableLiveData<String> = MutableLiveData()


    //获取设备属性列表
    fun getAllDeviceCategory(){
        requestCustom({
                      DeviceTypeApi.deviceTypeApi.getAllDeviceCategoryData()
        },{
            deviceCategoryResult.postValue(it)
        }){
            code, message ->
            dCategoryMsg.postValue(message)
        }
    }

}