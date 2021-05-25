package com.trx.consumer.screens.workout

import java.util.Locale

enum class WorkoutViewState {
    VIDEO, LIVE, VIRTUAL, UNKNOWN;

    companion object {
        const val LARGE_GROUP = "large-group"
        const val ONE_ON_ONE = "1-on-1"
        const val SMALL_GROUP = "small-group-training"
        const val VIDEO_ = "video"

        fun from(mode: String): WorkoutViewState {
            return when (mode.toLowerCase(Locale.ROOT)) {
                LARGE_GROUP -> LIVE
                ONE_ON_ONE -> VIRTUAL
                SMALL_GROUP -> VIRTUAL
                VIDEO_ -> VIDEO
                else -> UNKNOWN
            }
        }
    }
}
