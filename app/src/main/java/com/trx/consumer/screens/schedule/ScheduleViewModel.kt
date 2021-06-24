package com.trx.consumer.screens.schedule

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel.SCHEDULE_TRAINER_LIVE
import com.trx.consumer.models.common.AnalyticsPageModel.SCHEDULE_TRAINER_VIRTUAL
import com.trx.consumer.models.common.AnalyticsPageModel.SCHEDULE_USER_LIVE
import com.trx.consumer.models.common.AnalyticsPageModel.SCHEDULE_USER_VIRTUAL
import com.trx.consumer.models.common.TrainerScheduleModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.ScheduleViewState
import com.trx.consumer.models.states.ScheduleViewState.LIVE
import com.trx.consumer.models.states.ScheduleViewState.TRAINER_LIVE
import com.trx.consumer.models.states.ScheduleViewState.TRAINER_VIRTUAL
import com.trx.consumer.models.states.ScheduleViewState.USER_LIVE
import com.trx.consumer.models.states.ScheduleViewState.USER_VIRTUAL
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.trainerschedule.TrainerScheduleListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener

class ScheduleViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(),
    LiveWorkoutViewListener,
    VirtualWorkoutViewListener,
    TrainerScheduleListener {

    val eventLoadView = CommonLiveEvent<ScheduleViewState>()
    val eventTapBack = CommonLiveEvent<Void>()

    var state = LIVE

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        when (state) {
            TRAINER_LIVE -> { analyticsManager.trackPageView(SCHEDULE_TRAINER_LIVE) }
            TRAINER_VIRTUAL -> { analyticsManager.trackPageView(SCHEDULE_TRAINER_VIRTUAL) }
            USER_LIVE -> { analyticsManager.trackPageView(SCHEDULE_USER_LIVE) }
            USER_VIRTUAL -> { analyticsManager.trackPageView(SCHEDULE_USER_VIRTUAL) }
            else -> { LogManager.log("Invalid state: ${state.name}") }
        }

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
