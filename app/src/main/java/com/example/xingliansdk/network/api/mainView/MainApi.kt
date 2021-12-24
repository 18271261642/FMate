package com.example.xingliansdk.network.api.mainView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.login.LoginBean

class MainApi private constructor() : AppApi<MainInterface>() {
    companion object {
        val mMainApi: MainApi by lazy { MainApi() }
    }
    suspend fun getPersonalInfo(): BaseResult<LoginBean> {
        return apiInterface?.personalInfo()!!
    }

}