package com.app.fmate.network.api.appUpdate

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

class AppUpdateApi private constructor() : AppApi<AppUpdateInterface>() {
    companion object {
        val AppUpdateApi: AppUpdateApi by lazy { AppUpdateApi() }
    }
    suspend fun getApp(
        appName: String = "",
        versionCode: Int = 0
    ): BaseResult<AppUpdateBean> {
        return apiInterface?.findUpdate(appName, versionCode)!!
    }



    suspend  fun getToken(): BaseResult<String>? {
//       appUpdateApi.token=""
//        appUpdateApi.tokenKey="ceshi"
    return apiInterface?.loginToken()
    }


}