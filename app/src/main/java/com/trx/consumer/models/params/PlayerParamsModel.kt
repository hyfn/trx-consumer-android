package com.trx.consumer.models.params

import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.VideoModel

class PlayerParamsModel(
    var video: VideoModel = VideoModel.test().apply { id = "6232799349001" },
    var analyticsManager: AnalyticsManager
)
