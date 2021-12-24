package com.example.xingliansdk.network.api.meView

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.HashMap

interface MeViewInterface {
    @GET("/dial/find_lately_three_dial_image")
    suspend fun getDialImg(
    ): BaseResult<Any>
}