package com.trx.consumer.screens.trainer

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener

class TrainerViewModel : BaseViewModel(), LiveWorkoutListener, VideoWorkoutListener {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<TrainerModel>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView(trainerModel: TrainerModel) {
        eventLoadView.postValue(trainerModel)
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelect(model: VideoModel) {
    }
}
