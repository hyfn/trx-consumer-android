package com.trx.consumer.screens.email

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager

class EmailViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel() {

    var state: EmailViewState = EmailViewState.FORGOT

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadState = CommonLiveEvent<EmailViewState>()
    val eventLoadButton = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapSendEmail = CommonLiveEvent<EmailViewState>()
    val eventTapReset = CommonLiveEvent<Void>()

    val eventValidateError = CommonLiveEvent<String>()
    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.call()
        eventLoadState.postValue(state)
        eventLoadButton.postValue(false)
    }
}
