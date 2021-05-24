package com.trx.consumer.screens.profile

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkouttable.LiveWorkoutTableListener
import com.trx.consumer.screens.virtualworkouttable.VirtualWorkoutTableListener

class ProfileViewModel : BaseViewModel(), LiveWorkoutTableListener, VirtualWorkoutTableListener {

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

    override fun doTapBook(model: WorkoutModel) {
    }

    override fun doTapSelect(model: WorkoutModel) {
    }

    override fun doTapPrimary(model: VirtualWorkoutModel) {
    }

    override fun doTapSelect(model: VirtualWorkoutModel) {
    }
}
