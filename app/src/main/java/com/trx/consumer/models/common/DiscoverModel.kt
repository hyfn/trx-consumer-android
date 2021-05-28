package com.trx.consumer.models.common

import com.trx.consumer.screens.discover.DiscoverViewState

class DiscoverModel(
    val state: DiscoverViewState = DiscoverViewState.WORKOUTS,
    val workouts: List<VideoModel> = emptyList(),
    val videos: List<VideosModel> = emptyList()
)
