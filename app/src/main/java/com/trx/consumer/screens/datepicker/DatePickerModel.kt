package com.trx.consumer.screens.datepicker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
class DatePickerModel(
    val initialDate: Date? = null,
    val minimumDate: Date? = null,
    val maximumDate: Date? = null
) : Parcelable {

    var onDateSelected: ((date: Date) -> Unit)? = null
    var onDismissed: (() -> Unit)? = null

    fun setOnDateSelectedListener(listener: (date: Date) -> Unit) {
        onDateSelected = listener
    }

    fun setOnDismissedListener(listener: () -> Unit) {
        onDismissed = listener
    }
}
