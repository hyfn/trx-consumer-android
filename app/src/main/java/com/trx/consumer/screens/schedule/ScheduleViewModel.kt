package com.trx.consumer.screens.schedule

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.TrainerScheduleModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.ScheduleViewState
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.trainerschedule.TrainerScheduleListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener

class ScheduleViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) :
    BaseViewModel(),
    LiveWorkoutViewListener,
    VirtualWorkoutViewListener,
    TrainerScheduleListener {

    val eventLoadView = CommonLiveEvent<ScheduleViewState>()
    val eventTapBack = CommonLiveEvent<Void>()

    var state = ScheduleViewState.LIVE

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        analyticsManager.trackPageView(pageTitle)
        eventLoadView.postValue(state)
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapPrimaryVirtualWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectVirtualWorkout(model: WorkoutModel) {
    }

    override fun doTapClass(trainerScheduleModel: TrainerScheduleModel) {
    }
}
