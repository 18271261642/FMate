package com.app.fmate.network.api.meView

import com.app.fmate.network.BaseResult
import retrofit2.http.*

interface MeViewInterface {
    @GET("/dial/find_lately_three_dial_image")
    suspend fun getDialImg(@Query("productNumber")pNumber : String): BaseResult<Any>
}