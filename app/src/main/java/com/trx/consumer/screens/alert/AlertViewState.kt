package com.trx.consumer.screens.alert

import androidx.annotation.ColorRes
import com.trx.consumer.R
import com.trx.consumer.extensions.dp

enum class AlertViewState {

    POSITIVE,
    NEGATIVE,
    NEUTRAL,
    CLEAR;

    @get:ColorRes
    val titleColor: Int
        get() {
            return when (this) {
                POSITIVE -> R.color.white
                NEUTRAL -> R.color.black
                NEGATIVE -> R.color.white
                CLEAR -> R.color.grey
            }
        }

    @get:ColorRes
    val bgColor: Int
        get() {
            return when (this) {
                POSITIVE -> R.color.black
                NEUTRAL -> R.color.greyLight
                NEGATIVE -> R.color.red
                CLEAR -> R.color.transparent
            }
        }

    val font: Int
        get() = if (CLEAR == this) 14.dp else 13.dp

    val fontFamily: Int
        get() = if (CLEAR == this) R.font.atcarquette_regular else R.font.atcarquette_bold
}
