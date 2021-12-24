package com.example.xingliansdk.network.api.otaUpdate

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.POST
import retrofit2.http.Query

interface OTAInterface {
    @POST("/find_update")
    suspend fun findUpdate(@Query("productNumber") productNumber:String
                           ,@Query("versionCode") versionCode:Int
    ): BaseResult<OTAUpdateBean>
}