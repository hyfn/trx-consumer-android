package com.trx.consumer.screens.schedule

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.TrainerScheduleModel
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.ScheduleViewState
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.trainerschedule.TrainerScheduleListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutListener

class ScheduleViewModel :
    BaseViewModel(),
    LiveWorkoutListener,
    VirtualWorkoutListener,
    TrainerScheduleListener {

    val eventLoadView = CommonLiveEvent<ScheduleViewState>()
    val eventTapBack = CommonLiveEvent<Void>()

    var state = ScheduleViewState.LIVE

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(state)
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapPrimary(model: VirtualWorkoutModel) {
    }

    override fun doTapSelect(model: VirtualWorkoutModel) {
    }

    override fun doTapTrainerSchedule(trainerScheduleModel: TrainerScheduleModel) {
    }
}
