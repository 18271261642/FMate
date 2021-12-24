package com.example.xingliansdk.network.api.cardView

import androidx.lifecycle.MutableLiveData
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.requestCustom

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