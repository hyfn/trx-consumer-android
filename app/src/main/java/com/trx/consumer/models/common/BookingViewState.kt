package com.trx.consumer.models.common

import androidx.annotation.StringRes
import com.trx.consumer.R

enum class BookingViewState {
    BOOK,
    BOOKED,
    CANCELLED,
    DISABLED,
    VIEW;

    val show: Boolean
        get() = this == BOOKED || this == BOOK

    @get:StringRes
    val buttonTitle: Int
        get() {
            return when(this) {
                BOOK -> R.string.booking_view_state_book_label
                BOOKED -> R.string.booking_view_state_booked_label
                VIEW -> R.string.booking_view_state_view_label
                else -> R.string.booking_view_state_default_label
            }
        }
}