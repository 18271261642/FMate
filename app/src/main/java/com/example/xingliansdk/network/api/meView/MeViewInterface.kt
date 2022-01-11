package com.example.xingliansdk.network.api.meView

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*
import java.util.HashMap

interface MeViewInterface {
    @GET("/dial/find_lately_three_dial_image")
    suspend fun getDialImg(@Query("productNumber")pNumber : String): BaseResult<Any>
}