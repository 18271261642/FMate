package com.app.fmate.network.api.mainView

import com.app.fmate.network.BaseResult
import com.app.fmate.network.api.login.LoginBean
import retrofit2.http.GET

interface MainInterface {
    @GET("/user/personal_info")
    suspend fun personalInfo(): BaseResult<LoginBean>

}