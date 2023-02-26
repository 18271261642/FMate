package com.app.fmate.network.api.cardView

import androidx.lifecycle.MutableLiveData
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.network.requestCustom

open class CardViewModel : BaseViewModel() {
    val resultGetCard: MutableLiveData<EditCardVoBean> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()
    fun getAllCard() {
        requestCustom({
            CardViewApi.mCardViewApi.getAllCard()
        }, {
            resultGetCard.postValue(it)
        }) { code, message ->
            message?.let {
                msg.postValue(it)
            }
        }
    }
    val resultUpdate: MutableLiveData<Any> = MutableLiveData()
    fun updateCard(addList: String,moreList:String) {
        requestCustom({
            CardViewApi.mCardViewApi.updateCard(addList,moreList)
        }, {
            resultUpdate.postValue(it)
        }) { code, message ->
            message?.let {
                msg.postValue(it)
            }
        }
    }
}