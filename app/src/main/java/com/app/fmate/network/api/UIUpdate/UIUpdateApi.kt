package com.app.fmate.network.api.UIUpdate

import com.app.fmate.base.AppApi
import com.app.fmate.network.BaseResult

class UIUpdateApi private constructor():AppApi<UIUpdateInterface>(){
    companion object{
        val uiUpdateApi:UIUpdateApi by lazy { UIUpdateApi() }
    }
    suspend fun getUpdateZipFll(productNumber:String="",UUID:String=""): BaseResult<UIUpdateBean> {
        return apiInterface?.findUIUpdate(productNumber,UUID,UUID)!!
    }

}