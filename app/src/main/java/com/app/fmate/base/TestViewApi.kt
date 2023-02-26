package com.app.fmate.base

import com.app.fmate.network.BaseResult
import okhttp3.MultipartBody

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