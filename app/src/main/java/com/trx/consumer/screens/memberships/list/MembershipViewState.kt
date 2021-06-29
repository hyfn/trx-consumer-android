package com.trx.consumer.screens.memberships.list

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class MembershipViewState {
    BASE,
    CUSTOM,
    ACTIVE,
    CANCELLED;

    @get:ColorRes
    val buttonBgColor: Int
        get() = when (this) {
            BASE, ACTIVE, CANCELLED -> R.color.greyLight
            CUSTOM -> R.color.black
        }

    @get:ColorRes
    val buttonTextColor: Int
        get() = when (this) {
            BASE, ACTIVE, CANCELLED -> R.color.black
            CUSTOM -> R.color.white
        }

    @get:StringRes
    val buttonText: Int
        get() = when (this) {
            BASE, ACTIVE, CANCELLED -> R.string.memberships_current_membership_label
            CUSTOM -> R.string.memberships_choose_membership_label
        }

    @get:StringRes
    val cancelButtonText: Int
        get() = when (this) {
            CANCELLED -> R.string.memberships_cancelled_membership_label
            else -> R.string.memberships_cancel_membership_label
        }
}
