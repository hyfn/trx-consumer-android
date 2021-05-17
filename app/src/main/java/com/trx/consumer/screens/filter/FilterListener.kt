package com.trx.consumer.screens.filter

import com.trx.consumer.models.common.VideoFilterModel

interface FilterListener {
    fun doTapFilter(model: VideoFilterModel)
}