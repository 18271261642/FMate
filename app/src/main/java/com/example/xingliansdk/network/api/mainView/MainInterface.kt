package com.example.xingliansdk.network.api.mainView

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.login.LoginBean
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MainInterface {
    @GET("/user/personal_info")
    suspend fun personalInfo(): BaseResult<LoginBean>

}