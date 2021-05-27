package com.trx.consumer.models.states

import java.util.Locale

enum class WorkoutViewState {
    VIDEO, LIVE, VIRTUAL, UNKNOWN;

    companion object {
        const val LARGE_GROUP_MODE = "large-group"
        const val ONE_ON_ONE_MODE = "1-on-1"
        const val SMALL_GROUP_MODE = "small-group-training"
        const val VIDEO_MODE = "video"

        fun from(mode: String): WorkoutViewState {
            return when (mode.toLowerCase(Locale.ROOT)) {
                LARGE_GROUP_MODE -> LIVE
                ONE_ON_ONE_MODE -> VIRTUAL
                SMALL_GROUP_MODE -> VIRTUAL
                VIDEO_MODE -> VIDEO
                else -> UNKNOWN
            }
        }
    }
}
