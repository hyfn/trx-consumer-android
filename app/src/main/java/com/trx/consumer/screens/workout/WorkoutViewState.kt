package com.trx.consumer.screens.workout

import java.util.Locale

enum class WorkoutViewState {
    VIDEO, LIVE, VIRTUAL, UNKNOWN;

    companion object {
        object Constant {
            const val LARGE_GROUP = "large-group"
            const val ONE_ON_ONE = "1-on-1"
            const val SMALL_GROUP = "small-group-training"
            const val VIDEO = "video"
        }

        fun from(mode: String): WorkoutViewState {
            return when (mode.toLowerCase(Locale.ROOT)) {
                Constant.LARGE_GROUP -> LIVE
                Constant.ONE_ON_ONE -> VIRTUAL
                Constant.SMALL_GROUP -> VIRTUAL
                Constant.VIDEO -> VIDEO
                else -> UNKNOWN
            }
        }
    }
}
