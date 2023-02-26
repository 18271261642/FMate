package com.app.fmate.network.api.appUpdate

import com.app.fmate.network.BaseResult
import retrofit2.http.POST
import retrofit2.http.Query

interface AppUpdateInterface {
    @POST("/find_app_update")
    suspend fun findUpdate(@Query("appName") appName:String
                           ,@Query("versionCode") versionCode:Int
                           ,@Query("type")type:Int=1

    ): BaseResult<AppUpdateBean>

    @POST("/test2")
    suspend fun loginToken(): BaseResult<String>
}