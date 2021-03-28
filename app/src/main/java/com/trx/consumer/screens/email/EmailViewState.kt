package com.trx.consumer.screens.email

import androidx.annotation.ColorRes
import com.trx.consumer.R
import com.trx.consumer.views.input.InputViewState

enum class EmailViewState {
    FORGOT, VERIFY;

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

    val firstButtonHidden: Boolean
        get() = when (this) {
            FORGOT -> true
            VERIFY -> false
        }

    val firstButtonTitle: Int
        get() = when (this) {
            VERIFY -> R.string.email_verify_resend
            else -> R.string.content_blank
        }

    val secondButtonTitle: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_second
            VERIFY -> R.string.email_verify_second
        }

    val thirdButtonTitle: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_third
            VERIFY -> R.string.email_verify_third
        }

    companion object {
        @ColorRes
        fun secondButtonColor(enabled: Boolean): Int {
            return if (enabled) R.color.blueDark else R.color.greyDarkExtra
        }
    }
}
