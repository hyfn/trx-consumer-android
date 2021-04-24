package com.trx.consumer.screens.cards

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.CardModel

class CardsViewModel : BaseViewModel() {

    var model: CardModel? = null

    val eventLoadView = CommonLiveEvent<CardModel?>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    fun doLoadView() {
        eventLoadView.postValue(CardModel.test())
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun onBackPressed() {
        eventTapBack.call()
    }
}
