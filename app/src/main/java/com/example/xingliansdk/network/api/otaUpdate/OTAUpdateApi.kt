package com.example.xingliansdk.network.api.otaUpdate

import android.os.Build
import android.util.Config
import com.example.xingliansdk.BuildConfig
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.weather.bean.WeatherBean
import com.shon.connector.bean.DataBean
import com.shon.connector.utils.TLog
import com.shon.net.BaseApi

class OTAUpdateApi private constructor():AppApi<OTAInterface>(){
    companion object{
        val otaUpdateApi:OTAUpdateApi by lazy { OTAUpdateApi() }
    }
    suspend fun getUpdateZip(productNumber:String="",versionCode:Int=0): OTAUpdateBean? {
        val data= apiInterface?.findUpdate(productNumber,versionCode)
        TLog.error("data++"+data)
//        return if(data?.code==200) {
//            apiInterface?.findUpdate(key)?.data
//        } else {
//            apiInterface?.findUpdate(key).msg
//        }
        return apiInterface?.findUpdate(productNumber,versionCode)?.data
    }

    suspend fun getUpdateZipFll(productNumber:String="",versionCode:Int=0): BaseResult<OTAUpdateBean> {
        return apiInterface?.findUpdate(productNumber,versionCode)!!
    }

}