package com.trx.consumer.screens.splash

import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.IAPManager
import kotlinx.coroutines.launch

class SplashViewModel() : BaseViewModel() {

    //region Events
    val eventTapEmail = CommonLiveEvent<Void>()
    val eventTapSignUp = CommonLiveEvent<Void>()

    //endregion

    //region Functions

    fun doTapEmail() {
        viewModelScope.launch {
            IAPManager.shared.packages()
        }
    }

    fun doTapSignUp() {
        eventTapSignUp.call()
    }

    //endregion
}
