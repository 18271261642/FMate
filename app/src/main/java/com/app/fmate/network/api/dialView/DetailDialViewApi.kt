package com.app.fmate.network.api.dialView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

class DetailDialViewApi private constructor() : AppApi<RecommendDialViewInterface>() {
    companion object {
        val mDetailDialViewApi: DetailDialViewApi by lazy { DetailDialViewApi() }
    }
     suspend fun updateUserDial(hashMap: HashMap<String,String>):BaseResult<Any>
    {
      return  apiInterface?.updateUserDial(hashMap)!!
    }

}