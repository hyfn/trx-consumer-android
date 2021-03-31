package com.trx.consumer.screens.home

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class HomeViewModel : BaseViewModel() {

    //region Events

    val eventTapTest = CommonLiveEvent<Void>()

    //endregion

    //region Functions

    fun doTapTest() {
        eventTapTest.call()
    }

    //endregion
}
