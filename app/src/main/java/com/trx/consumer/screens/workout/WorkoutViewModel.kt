package com.trx.consumer.screens.workout

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.BookingViewState

class WorkoutViewModel : BaseViewModel() {

    var state: BookingViewState = BookingViewState.CANCEL

    val eventLoadView = CommonLiveEvent<BookingViewState>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(state)
    }
}
