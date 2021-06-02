package com.trx.consumer.screens.trainer

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener

class TrainerViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), LiveWorkoutViewListener, VideoWorkoutListener {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<TrainerModel>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView(trainerModel: TrainerModel) {
        analyticsManager.trackAmplitude(AnalyticsEventModel.PAGE_VIEW, pageTitle)
        eventLoadView.postValue(trainerModel)
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapVideo(model: VideoModel) {
    }
}
