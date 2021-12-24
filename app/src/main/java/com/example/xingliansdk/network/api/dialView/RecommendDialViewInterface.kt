package com.example.xingliansdk.network.api.dialView

import com.example.xingliansdk.network.BaseResult
import retrofit2.http.*
import java.util.HashMap

interface RecommendDialViewInterface {
    @FormUrlEncoded
    @POST("dial/find_dial")
    suspend fun findDialImg(
        @FieldMap value: HashMap<String, String>
    ): BaseResult<RecommendDialBean>

    @FormUrlEncoded
    @POST("dial/update_user_dial")
    suspend fun updateUserDial(
        @FieldMap value: HashMap<String, String>
    ): BaseResult<Any>

    @GET("dial/find_my_dial")
    suspend fun findMyDial(): BaseResult<DownDialModel>

    @FormUrlEncoded
    @POST("dial/check_dial_sate")
    suspend fun checkDialSate(
        @Field("dialDtoList") data: String
    ): BaseResult<Any>


    @FormUrlEncoded
    @POST("dial/delete_my_dial")
    suspend fun deleteMyDial(
        @Field("dialId") data: String
    ): BaseResult<Any>

}