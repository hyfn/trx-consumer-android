package com.trx.consumer.screens.splash

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class SplashViewModel() : BaseViewModel() {

    //region Events
    val eventLoadView = CommonLiveEvent<Void>()
    val eventTapFacebook = CommonLiveEvent<Void>()
    val eventTapGoogle = CommonLiveEvent<Void>()
    val eventTapApple = CommonLiveEvent<Void>()
    val eventTapEmail = CommonLiveEvent<Void>()

    //endregion

    //region Functions
    fun doLoadView() {
        eventLoadView.call()
    }

    fun doTapFacebook() {
        eventTapFacebook.call()
    }

    fun doTapGoogle() {
        eventTapGoogle.call()
    }

    fun doTapApple() {
        eventTapApple.call()
    }

    fun doTapEmail() {
        eventTapEmail.call()
    }

    //endregion
}
