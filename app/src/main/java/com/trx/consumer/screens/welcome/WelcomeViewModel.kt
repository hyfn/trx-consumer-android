package com.trx.consumer.screens.welcome

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class WelcomeViewModel : BaseViewModel() {

    var state: WelcomeState = WelcomeState.WELCOME

    val eventLoadView = CommonLiveEvent<WelcomeState>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapClose = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.postValue(state)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapClose() {
        eventTapClose.call()
    }
}
