package com.example.xingliansdk.base

import com.example.xingliansdk.network.BaseResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TestInterface {

    @Multipart
    @POST("/file/upload")
    suspend fun upLoadSingleFile(
        @Part file: MultipartBody.Part?
    ): BaseResult<String>
    @Multipart
    @POST("/file/upload")
    suspend  fun upLoadSingleFile(
        @Path("file") url: String?,
        @Body body: RequestBody?,
        @Part file: MultipartBody.Part?
    ): BaseResult<String>
    @Multipart
    @POST("/user/upload_head_portrait")
    suspend   fun upLoadFile(
        @Part file: MultipartBody.Part?
    ): Call<com.shon.net.BaseResult<*>?>?
}