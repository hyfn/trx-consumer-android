package com.trx.consumer.screens.email

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.views.input.InputViewState

enum class EmailViewState {
    FORGOT, CODE;

    @get:StringRes
    val headerTitle: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_header_title
            CODE -> R.string.email_code_header_title
        }

    @get:DrawableRes
    val headerImage: Int
        get() = when (this) {
            FORGOT -> R.drawable.img_media_email_forgot
            CODE -> R.drawable.img_media_email_code
        }

    val contentType: InputViewState
        get() = when (this) {
            FORGOT -> InputViewState.EMAIL
            CODE -> InputViewState.CODE
        }

    @get:StringRes
    val title: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_title
            CODE -> R.string.email_code_title
        }

    @get:StringRes
    val placeholder: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_placeholder
            CODE -> R.string.email_code_placeholder
        }

    @get:StringRes
    val description: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_description
            CODE -> R.string.email_code_description
        }

    @get:StringRes
    val buttonTitle: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_button_label
            CODE -> R.string.email_code_button_label
        }

    @get:StringRes
    val success: Int
        get() = when (this) {
            FORGOT -> R.string.email_forgot_success
            CODE -> R.string.email_code_success
        }

    val inputViewState: InputViewState
        get() = when (this) {
            FORGOT -> InputViewState.EMAIL
            CODE -> InputViewState.CODE
        }
}
