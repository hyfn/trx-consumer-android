package com.trx.consumer.screens.memberships.list

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class MembershipViewState {
    BASE,
    ACTIVE,
    PLAIN;

    @get:ColorRes
    val buttonBgColor: Int
        get() = when (this) {
            BASE, ACTIVE -> R.color.greyLight
            PLAIN -> R.color.black
        }

    @get:ColorRes
    val buttonTextColor: Int
        get() = when (this) {
            BASE, ACTIVE -> R.color.black
            PLAIN -> R.color.white
        }

    @get:StringRes
    val buttonText: Int
        get() = when (this) {
            BASE, ACTIVE -> R.string.memberships_current_membership_label
            PLAIN -> R.string.memberships_choose_membership_label
        }
}