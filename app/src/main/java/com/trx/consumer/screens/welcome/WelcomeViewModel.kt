package com.trx.consumer.screens.welcome

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager

class WelcomeViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    var state: WelcomeState = WelcomeState.WELCOME

    val eventLoadView = CommonLiveEvent<WelcomeState>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapClose = CommonLiveEvent<Void>()

    fun doLoadView() {
        analyticsManager.trackPageView(pageTitle)
        eventLoadView.postValue(state)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapClose() {
        eventTapClose.call()
    }
}
