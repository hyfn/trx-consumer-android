package com.trx.consumer.models.responses

import com.trx.consumer.extensions.forEach
import com.trx.consumer.models.common.WorkoutModel
import org.json.JSONObject

class BookingResponseModel(val workouts: MutableList<WorkoutModel> = mutableListOf()) {

    /* val calendarModelLive: CalendarModel
         get() = CalendarModel(CalendarViewState.PICKER).apply {
             listDays = CalendarModel.createListOfDays()
             listEvents = lstLiveUpcoming.map { it.date }
         }

     val calendarModelVirtual: CalendarModel
         get() = CalendarModel(CalendarViewState.DISPLAY).apply {
             listDays = CalendarModel.createListOfDays()
             listEvents = lstVirtualUpcoming.map { date }
         }*/

    val lstWorkoutsSorted: List<WorkoutModel>
        get() = workouts.filter { it.isCanceled }.sortedBy { it.startsAt }.map { it.booking }

    /*var lstLiveUpcoming: List<WorkoutModel>
        get() = lstWorkoutsSorted.filter { it.workoutState == WorkoutViewState.LIVE }
            .filter { it.date.(than: Date().dateByAddingValue(kMinutesAfterCanJoin, unit:.minute)) }


    var lstVirtualUpcoming: <WorkoutModel>
        get() =lstWorkoutsSorted.filter { $0.workoutState == . virtual }.filter { $0.date.isLater(than: Date()) }*/

    companion object {
        fun parse(json: String): BookingResponseModel {
            return BookingResponseModel().apply {
                val jsonObject = JSONObject(json)
                jsonObject.optJSONArray("data")?.forEach { items ->
                    workouts.add(WorkoutModel.parse(items))
                }
            }
        }
    }
}
