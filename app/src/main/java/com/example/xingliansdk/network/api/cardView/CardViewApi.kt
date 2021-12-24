package com.example.xingliansdk.network.api.cardView

import com.example.xingliansdk.base.AppApi
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.network.BaseResult
import com.google.gson.Gson
import com.shon.connector.utils.TLog
import java.util.*

class CardViewApi private constructor() : AppApi<CardInterface>() {
    companion object {
        val mCardViewApi: CardViewApi by lazy { CardViewApi() }
    }


    suspend fun getAllCard():BaseResult<EditCardVoBean>{
        return apiInterface?.getAllCard()!!
    }
    suspend fun updateCard(value: String,moreList:String):BaseResult<Any>{
        return apiInterface?.updateCard(value,moreList)!!
    }

}