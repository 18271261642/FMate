package com.app.fmate.network.api.meView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

class MeViewApi private constructor() : AppApi<MeViewInterface>() {
    companion object {
        val mMeViewApi: MeViewApi by lazy { MeViewApi() }
    }
     suspend fun getDialImg(nNumber : String ):BaseResult<Any>
    {
      return  apiInterface?.getDialImg(nNumber)!!
    }

}