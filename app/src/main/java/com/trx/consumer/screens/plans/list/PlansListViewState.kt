package com.trx.consumer.screens.plans.list

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class PlansListViewState {
    SELECTED,
    UNSELECTED;

    @get:ColorRes
    val buttonBgColor: Int
        get() = when (this) {
            SELECTED -> R.color.grey
            UNSELECTED -> R.color.black
        }

    @get:ColorRes
    val buttonTextColor: Int
        get() = when (this) {
            SELECTED -> R.color.black
            UNSELECTED -> R.color.white
        }

    @get:StringRes
    val buttonText: Int
        get() = when (this) {
            SELECTED -> R.string.plans_current_plan_label
            UNSELECTED -> R.string.plans_choose_plan_label
        }
}
