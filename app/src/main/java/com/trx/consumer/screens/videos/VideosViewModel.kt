package com.trx.consumer.screens.videos

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.screens.discover.list.DiscoverListener

class VideosViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), DiscoverListener {

    var model: VideosModel = VideosModel()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<VideosModel>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapProfile = CommonLiveEvent<TrainerModel>()
    val eventTapStartWorkout = CommonLiveEvent<VideoModel>()

    fun doLoadView() {
        analyticsManager.trackAmplitude(AnalyticsEventModel.PAGE_VIEW, pageTitle)
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
