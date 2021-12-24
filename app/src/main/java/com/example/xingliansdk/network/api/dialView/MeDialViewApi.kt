package com.example.xingliansdk.network.api.dialView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

class MeDialViewApi private constructor() : AppApi<RecommendDialViewInterface>() {
    companion object {
        val mMeDialViewApi: MeDialViewApi by lazy { MeDialViewApi() }
    }
     suspend fun findMyDial():BaseResult<DownDialModel>
    {
      return  apiInterface?.findMyDial()!!
    }
    suspend fun deleteMyDial(id:String):BaseResult<Any>
    {
        return  apiInterface?.deleteMyDial(id)!!
    }


}