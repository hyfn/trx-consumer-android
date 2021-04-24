package com.trx.consumer.models.common

import com.trx.consumer.extensions.yearMonthDayString
import com.trx.consumer.models.states.CalendarViewState
import java.util.Date

class CalendarModel(var state: CalendarViewState = CalendarViewState.DISPLAY) {

    var selectedDate: Date = Date()

    var listDays = listOf<DaysModel>()

    var listEvents = listOf<DaysModel>()

    var initialDate: Date? = listDays.firstOrNull()?.date

    var initialIndexPath: Int? = listDays.indexOfFirst {
        it.date.yearMonthDayString() == initialDate?.yearMonthDayString()
    }
}
