package com.trx.consumer.screens.plans.list

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class PlansViewState {
    CURRENT,
    OTHER;

    @get:ColorRes
    val buttonBgColor: Int
        get() = when (this) {
            CURRENT -> R.color.greyLight
            OTHER -> R.color.black
        }

    @get:ColorRes
    val buttonTextColor: Int
        get() = when (this) {
            CURRENT -> R.color.black
            OTHER -> R.color.white
        }

    @get:StringRes
    val buttonText: Int
        get() = when (this) {
            CURRENT -> R.string.plans_current_plan_label
            OTHER -> R.string.plans_choose_plan_label
        }
}
