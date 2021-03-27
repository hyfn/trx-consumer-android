package com.trx.consumer.screens.splash

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class SplashViewModel() : BaseViewModel() {

    //region Events
    val eventTapEmail = CommonLiveEvent<Void>()
    val eventTapSignUp = CommonLiveEvent<Void>()

    //endregion

    //region Functions

    fun doTapEmail() {
        eventTapEmail.call()
    }

    fun doTapSignUp() {
        eventTapSignUp.call()
    }

    //endregion
}
