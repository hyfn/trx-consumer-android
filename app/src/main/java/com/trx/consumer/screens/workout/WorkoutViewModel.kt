package com.trx.consumer.screens.workout

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.BookingState

class WorkoutViewModel : BaseViewModel() {

    var state: BookingState = BookingState.CANCEL

    val eventLoadView = CommonLiveEvent<BookingState>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(state)
    }
}
