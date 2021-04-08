package com.trx.consumer.models.common

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class VirtualBookingViewState {
    BOOK,
    SOON,
    FUTURE;

    @get:ColorRes
    val backgroundColor: Int
        get() {
            return when (this) {
                BOOK, FUTURE -> R.color.greyLightExtra
                SOON -> R.color.black
            }
        }

    @get:StringRes
    val buttonTitle: Int
        get() {
            return when (this) {
                BOOK -> R.string.virtual_booking_button_title_book
                SOON -> R.string.virtual_booking_button_title_soon
                FUTURE -> R.string.virtual_booking_button_title_future
            }
        }

    @get:ColorRes
    val buttonTitleColor: Int
        get() {
            return when (this) {
                SOON, FUTURE -> R.color.white
                BOOK -> R.color.black
            }
        }

    @get:ColorRes
    val buttonBackgroundColor: Int
        get() {
            return when (this) {
                SOON -> R.color.greyDark
                BOOK -> R.color.yellow
                FUTURE -> R.color.grey
            }
        }

    val buttonWidth: Float
        get() {
            return when (this) {
                BOOK, FUTURE -> 68f
                SOON -> 100f
            }
        }

    @get:ColorRes
    val titleColor: Int
        get() {
            return when (this) {
                BOOK, FUTURE -> R.color.black
                SOON -> R.color.white
            }
        }

    @get:ColorRes
    val subtitleColor: Int
        get() {
            return when (this) {
                BOOK, FUTURE -> R.color.grey
                SOON -> R.color.yellow
            }
        }
}
