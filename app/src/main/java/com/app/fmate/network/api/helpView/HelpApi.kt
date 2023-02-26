package com.app.fmate.network.api.helpView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
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