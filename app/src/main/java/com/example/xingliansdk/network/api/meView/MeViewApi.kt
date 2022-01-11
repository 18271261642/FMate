package com.example.xingliansdk.network.api.meView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.homeView.HomeViewApi
import com.example.xingliansdk.network.api.homeView.HomeViewInterface

class MeViewApi private constructor() : AppApi<MeViewInterface>() {
    companion object {
        val mMeViewApi: MeViewApi by lazy { MeViewApi() }
    }
     suspend fun getDialImg(nNumber : String ):BaseResult<Any>
    {
      return  apiInterface?.getDialImg(nNumber)!!
    }

}