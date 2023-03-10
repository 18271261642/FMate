package com.app.fmate.network.api.jignfan

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult
import com.app.fmate.ui.bp.MeasureBpBean
import java.util.HashMap

open class JingfanBpApi private constructor(): AppApi<JingfanBpInterface>(){


    companion object{
        val jingfanBpApi : JingfanBpApi by lazy { JingfanBpApi() }
    }


//    suspend fun markJFBp( data1 : String, data2 : String, data3 : String,sbp1 : Int,sbp2 : Int,sbp3 : Int,
//                          dbp1 : Int,dbp2 : Int,dbp3 : Int) : BaseResult<Any>{
//        val data = apiInterface?.markJingfanBp(data1,data2,data3,sbp1,sbp2,sbp3,dbp1,dbp2,dbp3)
//        return data!!
//    }

    suspend fun markJFBp( value: HashMap<String, Any>) : BaseResult<Any>{
        val data = apiInterface?.markJingfanBp(value)
        return data!!
    }


    suspend fun uploadJfBp(bpArray : String,time : String) : BaseResult<MeasureBpBean>{
        val data = apiInterface?.uploadJingfanBp(bpArray,time)
        return data!!
    }
}