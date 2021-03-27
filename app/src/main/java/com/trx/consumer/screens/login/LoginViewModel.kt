package com.trx.consumer.screens.login

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class LoginViewModel : BaseViewModel() {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()

    //endregion

    //region Functions

    fun doTapBack() {
        eventTapBack.call()
    }

    //endregion
}
