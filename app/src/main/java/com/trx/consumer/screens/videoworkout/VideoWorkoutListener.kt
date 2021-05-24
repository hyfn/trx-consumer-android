package com.trx.consumer.screens.videoworkout

import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel

interface VideoWorkoutListener {
    fun doTapVideo(model: VideoModel)
    fun doTapVideos(model: VideosModel) {}
}
