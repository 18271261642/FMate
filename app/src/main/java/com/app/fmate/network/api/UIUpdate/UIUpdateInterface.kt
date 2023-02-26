package com.app.fmate.network.api.UIUpdate

import com.app.fmate.network.BaseResult
import retrofit2.http.POST
import retrofit2.http.Query

interface UIUpdateInterface {
    @POST("/find_ui_update")
    suspend fun findUIUpdate(@Query("productNumber") productNumber:String
                           ,@Query("versionCode") versionCode:String
                             ,@Query("uiId") uiId:String
    ): BaseResult<UIUpdateBean>
}