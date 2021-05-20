package com.trx.consumer.screens.alert

import androidx.annotation.ColorRes
import com.trx.consumer.R

enum class AlertViewState {

    POSITIVE,
    NEGATIVE,
    NEUTRAL;

    @get:ColorRes
    val titleColor: Int
        get() {
            return when (this) {
                POSITIVE -> R.color.white
                NEUTRAL -> R.color.black
                NEGATIVE -> R.color.white
            }
        }

    @get:ColorRes
    val bgColor: Int
        get() {
            return when (this) {
                POSITIVE -> R.color.black
                NEUTRAL -> R.color.greyLight
                NEGATIVE -> R.color.red
            }
        }
}
