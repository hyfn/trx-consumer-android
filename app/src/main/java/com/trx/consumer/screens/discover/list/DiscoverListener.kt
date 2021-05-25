package com.trx.consumer.screens.discover.list

import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel

interface DiscoverListener {
    fun doTapVideo(model: VideoModel)
    fun doTapVideos(model: VideosModel) {}
}
