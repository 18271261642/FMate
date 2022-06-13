package com.example.xingliansdk.network.api.UIUpdate

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

class UIUpdateApi private constructor():AppApi<UIUpdateInterface>(){
    companion object{
        val uiUpdateApi:UIUpdateApi by lazy { UIUpdateApi() }
    }
    suspend fun getUpdateZipFll(productNumber:String="",UUID:String=""): BaseResult<UIUpdateBean> {
        return apiInterface?.findUIUpdate(productNumber,UUID,UUID)!!
    }

}