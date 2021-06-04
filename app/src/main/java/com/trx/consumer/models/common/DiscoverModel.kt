package com.trx.consumer.models.common

import com.trx.consumer.screens.discover.DiscoverViewState

class DiscoverModel(
    var state: DiscoverViewState = DiscoverViewState.WORKOUTS,
    var workouts: List<VideoModel> = emptyList(),
    var collections: List<VideosModel> = emptyList(),
    var programs: List<VideosModel> = emptyList()
) {

    companion object {

        fun skeleton(
            state: DiscoverViewState = DiscoverViewState.WORKOUTS,
            count: Int = 5
        ): DiscoverModel {
            return DiscoverModel(
                state = state,
                workouts = VideoModel.skeletonList(count),
                collections = VideosModel.skeletonList(count),
                programs = VideosModel.skeletonList(count)
            )
        }
    }
}
