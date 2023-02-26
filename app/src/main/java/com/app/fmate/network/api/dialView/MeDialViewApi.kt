package com.app.fmate.network.api.dialView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

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