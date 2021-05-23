package com.trx.consumer.screens.videos

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.discover.list.DiscoverListener

class VideosViewModel : BaseViewModel(), DiscoverListener {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<VideosModel>()

    lateinit var videosModel: VideosModel

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(videosModel)
    }

    override fun doTapDiscoverWorkout(model: VideoModel) {
        TODO("Not yet implemented")
    }

    override fun doTapDiscoverCollections(model: VideosModel) {
        TODO("Not yet implemented")
    }


}
