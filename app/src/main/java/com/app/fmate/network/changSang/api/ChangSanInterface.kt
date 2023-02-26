package com.app.fmate.network.changSang.api

import com.app.fmate.network.BaseResult
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