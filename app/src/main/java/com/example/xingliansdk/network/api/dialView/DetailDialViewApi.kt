package com.example.xingliansdk.network.api.dialView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

class DetailDialViewApi private constructor() : AppApi<RecommendDialViewInterface>() {
    companion object {
        val mDetailDialViewApi: DetailDialViewApi by lazy { DetailDialViewApi() }
    }
     suspend fun updateUserDial(hashMap: HashMap<String,String>):BaseResult<Any>
    {
      return  apiInterface?.updateUserDial(hashMap)!!
    }

}