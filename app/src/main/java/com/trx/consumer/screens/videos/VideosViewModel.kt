package com.trx.consumer.screens.videos

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.screens.discover.list.DiscoverListener

class VideosViewModel : BaseViewModel(), DiscoverListener {

    var model: VideosModel = VideosModel()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<VideosModel>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapProfile = CommonLiveEvent<TrainerModel>()
    val eventTapStartWorkout = CommonLiveEvent<VideoModel>()

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
            eventTapStartWorkout.postValue(video)
        }
    }

    override fun doTapVideo(model: VideoModel) {
        eventTapVideo.postValue(model)
    }
}
