package com.example.xingliansdk.network.api.jignfan

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import java.util.HashMap

open class JingfanBpApi private constructor() : AppApi<JingfanBpInterface>(){


    companion object{
        val jingfanBpApi : JingfanBpApi by lazy { JingfanBpApi() }
    }


    suspend fun markJFBp(value: HashMap<String, String>) : BaseResult<Any>{
        val data = apiInterface?.markJingfanBp(value)
        return data!!
    }
}