package com.trx.consumer.screens.workout

import java.util.Locale

enum class WorkoutViewState {
    VIDEO, LIVE, VIRTUAL, UNKNOWN;

    companion object {
        const val largeGroup = "large-group"
        const val oneOnOne = "1-on-1"
        const val smallGroup = "small-group-training"
        const val video = "video"

        fun from(mode: String): WorkoutViewState {
            return when (mode.toLowerCase(Locale.ROOT)) {
                largeGroup -> LIVE
                oneOnOne -> VIRTUAL
                smallGroup -> VIRTUAL
                video -> VIDEO
                else -> UNKNOWN
            }
        }
    }
}
