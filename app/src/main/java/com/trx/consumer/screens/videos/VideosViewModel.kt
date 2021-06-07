package com.trx.consumer.screens.videos

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener

class VideosViewModel : BaseViewModel(), VideoWorkoutListener {

    var model: VideosModel = VideosModel()

    val eventLoadView = CommonLiveEvent<VideosModel>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapProfile = CommonLiveEvent<TrainerModel>()

    fun doLoadView() {
        eventLoadView.postValue(model)
    }

    override fun doTapVideo(model: VideoModel) {
        eventTapVideo.postValue(model)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapProfile() {
        eventTapProfile.postValue(model.trainer)
    }
}
