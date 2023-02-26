package com.app.fmate.network.api.otaUpdate

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.shon.connector.utils.TLog

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