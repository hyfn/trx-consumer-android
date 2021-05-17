package com.trx.consumer.screens.video.list

import com.trx.consumer.models.common.WorkoutModel

interface VideoListener {
    fun doTapVideo(model: WorkoutModel)
}
