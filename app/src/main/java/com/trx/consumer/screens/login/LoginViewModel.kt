package com.trx.consumer.screens.login

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class LoginViewModel : BaseViewModel() {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapSignUp = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapSignUp() {
        eventTapSignUp.call()
    }
}
