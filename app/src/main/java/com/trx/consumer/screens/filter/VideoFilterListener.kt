package com.trx.consumer.screens.filter

import com.trx.consumer.models.common.VideoFilterModel

interface VideoFilterListener {
    fun doTapFilter(model: VideoFilterModel)
}
