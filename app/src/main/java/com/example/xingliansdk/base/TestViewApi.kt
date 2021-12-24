package com.example.xingliansdk.base

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.network.BaseResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Part
import retrofit2.http.Path

class TestViewApi private constructor() : TestApi<TestInterface>() {
    companion object {
        val AppUpdateApi: TestViewApi by lazy { TestViewApi() }
    }




    suspend  fun setFile(file: MultipartBody.Part): BaseResult<String>? {

    return apiInterface?.upLoadSingleFile(file)
    }
        //这个是错误的传输方式
//    suspend  fun setFile( url: String,body: RequestBody, file: MultipartBody.Part): BaseResult<String>? {
//
//
//        return apiInterface?.upLoadSingleFile(url,body,file)
//    }
}