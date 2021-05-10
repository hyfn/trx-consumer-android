package com.trx.consumer.screens.trainer

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class TrainerViewModel : BaseViewModel() {

    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }
}
