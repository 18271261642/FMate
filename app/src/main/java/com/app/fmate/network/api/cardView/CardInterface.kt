package com.app.fmate.network.api.cardView

import com.app.fmate.network.BaseResult
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