package com.trx.consumer.screens.testutility

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class TestUtilityViewModel : BaseViewModel() {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()

    //endregion

    //region Functions

    fun doTapBack() {
        eventTapBack.call()
    }

    //endregion
}
