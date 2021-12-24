package com.example.xingliansdk.network.api.cardView

import android.view.ViewDebug
import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*
import java.util.*

interface CardInterface {
    @GET("/index_card/get_all_card")
    suspend fun getAllCard(
    ): BaseResult<EditCardVoBean>
    @FormUrlEncoded
    @POST("/index_card/update_card")
    suspend fun updateCard(
        @Field("addedList") addedList:String,
        @Field("moreList") moreList:String
    ): BaseResult<Any>

}