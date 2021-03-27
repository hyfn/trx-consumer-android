package com.trx.consumer.screens.register

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class RegisterViewModel : BaseViewModel() {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()

    //endregion

    //region Functions

    fun doTapBack() {
        eventTapBack.call()
    }

    //endregion
}
