package com.trx.consumer.models.common

import com.trx.consumer.extensions.yearMonthDayString
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.states.CalendarViewState
import java.util.Calendar
import java.util.Date

class CalendarModel(var state: CalendarViewState = CalendarViewState.DISPLAY) {

    var selectedDate: Date = Date()

    var listDays = listOf<DaysModel>()

    var listEvents = listOf<DaysModel>()

    var initialDate: Date? = listDays.firstOrNull()?.date

    var initialIndexPath: Int? = listDays.indexOfFirst {
        it.date.yearMonthDayString() == initialDate?.yearMonthDayString()
    }

    fun createListOfDays(
        startDate: Date = Date(),
        endDate: Date? = null,
        numberOfDays: Int = 7,
        selectedDate: Date? = null,
        state: CalendarViewState = CalendarViewState.DISPLAY
    ): List<DaysModel> {
        val list: List<DaysModel> = listOf()
        val calendar = Calendar.getInstance()
        val end = numberOfDays -1
        endDate?.let { safeEndDate ->
            calendar.time = startDate
            while (calendar.time == safeEndDate) {
               calendar.add(Calendar.DATE, 1)
               LogManager.log("${calendar.time}")
            }


        }

        return list
    }
}
