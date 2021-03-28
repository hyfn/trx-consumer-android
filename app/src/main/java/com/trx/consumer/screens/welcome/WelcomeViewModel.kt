package com.trx.consumer.screens.welcome

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class WelcomeViewModel : BaseViewModel() {

    val eventLoadView = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapClose = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapClose() {
        eventTapClose.call()
    }
}
