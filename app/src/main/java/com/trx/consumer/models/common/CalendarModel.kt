package com.trx.consumer.models.common

import com.trx.consumer.extensions.isSameDay
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

    companion object {
        fun createListOfDays(
            startDate: Date = Date(),
            endDate: Date? = null,
            numberOfDays: Int = 7,
            selectedDate: Date? = null,
            state: CalendarViewState = CalendarViewState.DISPLAY
        ): List<DaysModel> {
            val list: MutableList<DaysModel> = mutableListOf()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, numberOfDays - 1)
            var end = calendar.time
            endDate?.let { end = it }
            calendar.time = startDate
            while (calendar.time <= end) {
                LogManager.log("${calendar.time}")
                val date = calendar.time
                val item = DaysModel(date).apply { this.calendarState = state }
                selectedDate?.let { if (it.isSameDay(date)) item.state.isSelected = true }
                list.add(item)
                calendar.add(Calendar.DATE, 1)
            }
            return list
        }
    }
}
