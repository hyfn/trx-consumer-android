package com.trx.consumer.models.common

import com.trx.consumer.extensions.monthDayString
import com.trx.consumer.extensions.weekdayInitialString
import com.trx.consumer.models.states.CalendarViewState
import com.trx.consumer.models.states.DaysViewHolderState
import java.util.Date

class DaysModel(
    var date: Date
) {
    var state = DaysViewHolderState()
    var calendarState: CalendarViewState = CalendarViewState.DISPLAY

    val weekDayInitial: String
        get() = date.weekdayInitialString()

    val dayNumber: String
        get() = date.monthDayString()
}
