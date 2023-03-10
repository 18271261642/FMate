package com.app.fmate.network.changSang.api

import com.app.fmate.base.ChangSangAppApi
import com.app.fmate.network.BaseResult
import java.util.HashMap

class ChangSangApi private constructor():ChangSangAppApi<ChangSanInterface>(){
    companion object{
        val  mChangSangApi:ChangSangApi by lazy { ChangSangApi() }  //延迟初始化 在用的时候初始化
    }

    suspend fun  login(value: HashMap<String, String>): BaseResult<Any>
    {
        return apiInterface?.login(value)!!
    }
    suspend fun  updateLoad(value: HashMap<String, String>): BaseResult<ChangSangBean>
    {
        return apiInterface?.updateLoad(value)!!
    }
    suspend fun  fetchPar(value: HashMap<String, String>): BaseResult<Any>
    {
        return apiInterface?.fetchPar(value)!!
    }
}