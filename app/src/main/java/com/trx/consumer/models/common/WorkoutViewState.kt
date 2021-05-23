package com.trx.consumer.models.common

import com.trx.consumer.extensions.lowerCased

enum class WorkoutViewState {

    VIDEO, LIVE, VIRTUAL, UNKNOWN;

    companion object {

        fun from(mode: String): WorkoutViewState {
            return when (mode.lowerCased()) {
                largeGroup -> LIVE
                oneOnOne, smallGroup -> VIRTUAL
                video -> VIDEO
                else -> UNKNOWN
            }
        }

        private const val largeGroup = "large-group"
        private const val oneOnOne = "1-on-1"
        private const val smallGroup = "small-group-training"
        private const val video = "video"
    }
}