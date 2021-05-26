package com.trx.consumer.screens.profile

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutListener

class ProfileViewModel : BaseViewModel(), LiveWorkoutListener, VirtualWorkoutListener {

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

    override fun doTapPrimary(model: VirtualWorkoutModel) {
    }

    override fun doTapSelect(model: VirtualWorkoutModel) {
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }
}
