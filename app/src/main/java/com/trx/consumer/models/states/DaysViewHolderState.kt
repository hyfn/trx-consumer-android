package com.trx.consumer.models.states

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.trx.consumer.R

class DaysViewHolderState(
    var isSelected: Boolean = false
) {
    @get:ColorRes
    val bgColor: Int
        get() = if (isSelected) R.color.transparent else R.color.blue

    @get:ColorRes
    val textColor: Int
        get() = if (isSelected) R.color.blackRussian else R.color.greyDark

    @get:ColorRes
    val dotColor: Int
        get() = when {
            isSelected -> R.color.blue
            else -> R.color.transparent
        }

    @get:DrawableRes
    val bgDrawable: Int
        get() = if (isSelected) R.drawable.ic_circle_selected else R.drawable.bg_calendar_transparent
}
