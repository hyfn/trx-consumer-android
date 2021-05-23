package com.trx.consumer.screens.videos

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.screens.discover.list.DiscoverListener

class VideosViewModel : BaseViewModel(), DiscoverListener {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<VideosModel>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()

     var model: VideosModel = VideosModel()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(model)
    }

    override fun doTapVideo(model: VideoModel) {
        eventTapVideo.postValue(model)
    }

}
