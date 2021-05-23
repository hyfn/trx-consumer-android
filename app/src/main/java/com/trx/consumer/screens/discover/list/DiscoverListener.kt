package com.trx.consumer.screens.discover.list

import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel

interface DiscoverListener {
    fun doTapDiscoverWorkout(model: VideoModel)
    fun doTapDiscoverCollections(model: VideosModel)
}
