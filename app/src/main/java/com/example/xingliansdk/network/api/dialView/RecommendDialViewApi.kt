package com.example.xingliansdk.network.api.dialView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult

class RecommendDialViewApi private constructor() : AppApi<RecommendDialViewInterface>() {
    companion object {
        val mRecommendDialViewApi: RecommendDialViewApi by lazy { RecommendDialViewApi() }
    }
     suspend fun findDial(hashMap: HashMap<String,String>):BaseResult<RecommendDialBean>
    {
      return  apiInterface?.findDialImg(hashMap)!!
    }
    suspend fun checkDialSate(data:String):BaseResult<Any>
    {
        return  apiInterface?.checkDialSate(data)!!
    }


}