package com.trx.consumer.models.states

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class BookingViewState {
    BOOK,
    JOIN,
    CANCEL,
    VIDEO;

    @get:StringRes
    val buttonTitle: Int
        get() {
            return when (this) {
                BOOK -> R.string.booking_view_state_book_label
                JOIN -> R.string.booking_view_state_join_label
                CANCEL -> R.string.booking_view_state_cancel_label
                VIDEO -> R.string.booking_view_state_video_label
            }
        }

    @get:ColorRes
    val buttonTitleColor: Int
        get() {
            return when (this) {
                BOOK -> R.color.white
                JOIN -> R.color.white
                VIDEO -> R.color.white
                CANCEL -> R.color.red
            }
        }

    @get:ColorRes
    val buttonBackgroundColor: Int
        get() {
            return when (this) {
                BOOK -> R.color.black
                JOIN -> R.color.black
                VIDEO -> R.color.black
                CANCEL -> R.color.redLight
            }
        }
}
