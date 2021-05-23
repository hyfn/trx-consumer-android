package com.trx.consumer.models.states

import androidx.annotation.DrawableRes
import com.trx.consumer.R

class DaysViewHolderState(
    var isSelected: Boolean = false,
    var hasEvent: Boolean = false
) {
    @get:DrawableRes
    val bgDrawable: Int
        get() = when {
            isSelected -> R.drawable.ic_circle_selected
            hasEvent -> R.drawable.ic_circle
            else -> R.drawable.bg_calendar_transparent
        }
}
