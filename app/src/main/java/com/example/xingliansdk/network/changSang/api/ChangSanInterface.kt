package com.example.xingliansdk.network.changSang.api

import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.login.LoginBean
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.HashMap

interface ChangSanInterface {
    @POST("gromit/entry/enterpriselogin")
    suspend fun login(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>

    @POST("gromit/multi/calibrate/updateload/v2/s2")
    suspend fun updateLoad(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<ChangSangBean>

    @GET("gromit/account/{pid}/mulcal/foreign/calibrate/fetchpar/s1")
    suspend fun fetchPar(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>
}