package com.trx.consumer.models.states

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class BookingState {
    BOOK,
    BOOKED,
    CANCEL,
    JOIN,
    VIDEO,
    VIEW,
    DISABLED;

    val show: Boolean
        get() = this == BOOKED || this == BOOK

    @get:StringRes
    val buttonTitle: Int
        get() {
            return when (this) {
                BOOK -> R.string.booking_view_state_book_label
                BOOKED -> R.string.booking_view_state_booked_label
                JOIN -> R.string.booking_view_state_join_label
                CANCEL -> R.string.booking_view_state_cancel_label
                VIDEO -> R.string.booking_view_state_video_label
                VIEW -> R.string.booking_view_state_view_label
                else -> R.string.booking_view_state_default_label
            }
        }

    @get:ColorRes
    val buttonTitleColor: Int
        get() {
            return when (this) {
                BOOK -> R.color.white
                BOOKED -> R.color.white
                JOIN -> R.color.white
                VIDEO -> R.color.white
                VIEW -> R.color.white
                CANCEL -> R.color.red
                else -> R.color.white
            }
        }

    @get:ColorRes
    val buttonBackgroundColor: Int
        get() {
            return when (this) {
                BOOK -> R.color.black
                BOOKED -> R.color.black
                JOIN -> R.color.black
                VIDEO -> R.color.black
                VIEW -> R.color.black
                CANCEL -> R.color.redLight
                else -> R.color.black
            }
        }
}
