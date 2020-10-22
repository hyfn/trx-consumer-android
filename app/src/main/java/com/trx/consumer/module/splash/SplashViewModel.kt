package com.trx.consumer.module.splash

import com.trx.consumer.common.SingleLiveEvent
import com.trx.consumer.core.BaseViewModel
import com.trx.consumer.services.user.UserDataSource
import com.trx.consumer.services.user.UserRepository

class SplashViewModel(
    private val userDataSource: UserDataSource = UserRepository()
) : BaseViewModel() {

    val eventLoadView = SingleLiveEvent<Void>()
    val eventTapStart = SingleLiveEvent<Void>()

    fun onLoadView() {
        eventLoadView.call()
    }

    fun onTapStart() {
        eventTapStart.call()
    }
}
