package com.app.fmate.network.api.login

import com.app.fmate.network.BaseResult
import okhttp3.MultipartBody
import retrofit2.http.*
import java.util.*

interface LoginInterface {
    @POST("/get_verify_code")
    suspend fun getVersionCode(
        @Query("phone") phone: String,
        @Query("areaCode") areaCode: String,
        @Query("password") password: String,
        @Query("type") type: String
    ): BaseResult<LoginBean>

    @POST("/login")
    suspend fun login(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<LoginBean>

    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("areaCode") areaCode: String,
        @Field("verifyCode") verifyCode: String
    ): BaseResult<LoginBean>

    @POST("/user/update")
    suspend fun userUpdate(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<LoginBean>

    @GET("/logout")
    suspend fun outLogin(): BaseResult<Any>

    @POST("/user/update_password")
    suspend fun updatePassword(@QueryMap value: HashMap<String, String>): BaseResult<LoginBean>

    @POST("/user/update_phone")
    suspend fun updatePhone(@QueryMap value: HashMap<String, String>): BaseResult<Any>

    @POST("/user/save_appeal")
    suspend fun saveAppeal(@QueryMap value: HashMap<String, String>): BaseResult<Any>

    @FormUrlEncoded
    @POST("/user/delete")
    suspend fun userDelete(@Field("verifyCode") verifyCode: String): BaseResult<Any>

    //    @FormUrlEncoded
//    @POST("/user/upload_head_portrait")
//    suspend fun setHead(
//     @Field   file: File("file",String,file)
//
//    ):BaseResult<Any>
    @POST("/user/check_password")
    suspend fun checkPassword(@QueryMap value: HashMap<String, String>): BaseResult<Any>

    @GET("/check_verify_code")
    suspend fun checkVerifyCode(@QueryMap value: HashMap<String, String>): BaseResult<Any>

    /**
     * 带参数的单文件上传
     *
     * @param body 参数
     * @param file 文件
     * @return Call<ResponseBody>
     */
    @Multipart
    @POST("/user/upload_head_portrait")
    suspend fun upLoadSingleFile(
        @Part file: MultipartBody.Part?
    ): BaseResult<Any>


}