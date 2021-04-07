package com.trx.consumer.screens.email

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.views.input.InputViewState

enum class EmailViewState {
    FORGOT, VERIFY;

    @get:StringRes
    val title: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_title
            VERIFY -> R.string.email_verify_title
        }

    val contentType: InputViewState
        get() = when (this) {
            FORGOT -> InputViewState.EMAIL
            VERIFY -> InputViewState.CODE
        }

    @get:StringRes
    val buttonTitle: Int
        get() = when (this) {
            FORGOT -> R.string.email_lookup_second
            VERIFY -> R.string.email_done_button_label
            else -> R.string.content_blank
        }

    @get:StringRes
    val successButtonTitle: Int
        get() = R.string.email_done_button_label

    @get:ColorRes
    val successButtonBgColor: Int
        get() = R.color.greyLightExtra

    @get:ColorRes
    val successButtonTextColor: Int
        get() = R.color.blackRussian

    @get:ColorRes
    val normalButtonTextColor: Int
        get() = R.color.white

    @get:ColorRes
    val normalButtonBgColor: Int
        get() = R.color.blackRussian
}
