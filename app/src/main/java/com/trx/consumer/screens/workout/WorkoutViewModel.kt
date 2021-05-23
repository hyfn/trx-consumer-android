package com.trx.consumer.screens.workout

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.WorkoutModel

class WorkoutViewModel : BaseViewModel() {

    var model: WorkoutModel = WorkoutModel()

    val eventLoadView = CommonLiveEvent<WorkoutModel>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(model)
    }
}
