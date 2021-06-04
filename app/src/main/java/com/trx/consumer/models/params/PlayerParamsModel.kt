package com.trx.consumer.models.params

import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel

class PlayerParamsModel(
    var video: VideoModel,
    var analyticsManager: AnalyticsManager,
    var workout: WorkoutModel = WorkoutModel.testLive()
)
