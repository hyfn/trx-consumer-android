package com.trx.consumer.screens.splash

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class SplashViewModel() : BaseViewModel() {

    //region Events
    val eventLoadView = CommonLiveEvent<Void>()
    val eventTapStart = CommonLiveEvent<Void>()
    //endregion

    //region Functions
    fun onLoadView() {
        eventLoadView.call()
    }

    fun onTapStart() {
        eventTapStart.call()
    }
    //endregion
}
