package com.example.xingliansdk.network.api.helpView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.bean.UpdateWeight
import java.util.*

open class HelpApi private constructor() : AppApi<HelpInterface>() {
    companion object {
        val mHelpApi: HelpApi by lazy { HelpApi() }
    }


    suspend fun saveFeedback(value: HashMap<String, Any>): BaseResult<Any> {
        val data = apiInterface?.saveFeedback(value)
        return data!!
    }

}