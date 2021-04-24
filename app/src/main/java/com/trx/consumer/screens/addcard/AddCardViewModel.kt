package com.trx.consumer.screens.addcard

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class AddCardViewModel : BaseViewModel() {

    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }
}
