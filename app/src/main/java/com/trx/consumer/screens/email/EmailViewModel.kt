package com.trx.consumer.screens.email

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class EmailViewModel : BaseViewModel() {

    var state: EmailViewState = EmailViewState.FORGOT

    val eventLoadView = CommonLiveEvent<EmailViewState>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(state)
    }
}
