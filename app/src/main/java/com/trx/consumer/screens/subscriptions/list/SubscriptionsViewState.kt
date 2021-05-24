package com.trx.consumer.screens.subscriptions.list

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class SubscriptionsViewState {
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
            CURRENT -> R.string.subscriptions_current_subscription_label
            OTHER -> R.string.subscriptions_choose_subscription_label
        }
}
