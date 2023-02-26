package com.app.fmate.network.api.cardView

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

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