package com.trx.consumer.screens.plans

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class PlansViewModel : BaseViewModel() {

    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }
}
