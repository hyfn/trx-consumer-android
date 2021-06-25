package com.trx.consumer.screens.profile

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.AnalyticsPageModel.PROFILE
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener

class ProfileViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), LiveWorkoutViewListener, VirtualWorkoutViewListener {

    val eventLoadView = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapSettings = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doTapSettings() {
        eventTapSettings.call()
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapPrimaryVirtualWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectVirtualWorkout(model: WorkoutModel) {
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(PROFILE)
    }
}
