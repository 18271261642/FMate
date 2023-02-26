package com.app.fmate.network.api.mainView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.app.fmate.network.api.login.LoginBean

class MainApi private constructor() : AppApi<MainInterface>() {
    companion object {
        val mMainApi: MainApi by lazy { MainApi() }
    }
    suspend fun getPersonalInfo(): BaseResult<LoginBean> {
        return apiInterface?.personalInfo()!!
    }

}