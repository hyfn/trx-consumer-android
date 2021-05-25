package com.trx.consumer.screens.videos

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.screens.discover.list.DiscoverListener

class VideosViewModel : BaseViewModel(), DiscoverListener {

    var model: VideosModel = VideosModel()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<VideosModel>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapProfile = CommonLiveEvent<TrainerModel>()
    val eventTapStartWorkout = CommonLiveEvent<WorkoutModel>()

    fun doLoadView() {
        eventLoadView.postValue(model)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapProfile() {
        eventTapProfile.postValue(model.trainer)
    }

    fun doTapPrimary() {
        model.videos.firstOrNull()?.let { video ->
            val workout = WorkoutModel().apply {
                this.video = video
                this.state = BookingState.DISABLED
            }
            eventTapStartWorkout.postValue(workout)
        }
    }

    override fun doTapVideo(model: VideoModel) {
        eventTapVideo.postValue(model)
    }
}
