package com.trx.consumer.models.responses

import com.trx.consumer.extensions.forEach
import com.trx.consumer.models.common.WorkoutModel
import org.json.JSONObject

class BookingsResponseModel(val workouts: MutableList<WorkoutModel> = mutableListOf()) {

    /* val calendarModelLive: CalendarModel
    TODO: need to implement proper logic
         get() = CalendarModel(CalendarViewState.PICKER).apply {
             listDays = CalendarModel.createListOfDays()
             listEvents = lstLiveUpcoming.map { it.date }
         }

     val calendarModelVirtual: CalendarModel
     TODO: need to implement proper logic
         get() = CalendarModel(CalendarViewState.DISPLAY).apply {
             listDays = CalendarModel.createListOfDays()
             listEvents = lstVirtualUpcoming.map { date }
         }*/

    val lstWorkoutsSorted: List<WorkoutModel>
        get() = workouts.filter { !it.isCancelled }.sortedBy { it.startsAt }.map { it.booking }

    /*var lstLiveUpcoming: List<WorkoutModel>
    TODO: need to implement proper logic
        get() = lstWorkoutsSorted.filter { it.workoutState == WorkoutViewState.LIVE }
            .filter { it.date.(than: Date().dateByAddingValue(kMinutesAfterCanJoin, unit:.minute)) }


    var lstVirtualUpcoming: <WorkoutModel>
    TODO: need to implement proper logic
        get() =lstWorkoutsSorted.filter { $0.workoutState == . virtual }.filter { $0.date.isLater(than: Date()) }*/

    companion object {
        fun parse(json: String): BookingsResponseModel {
            return BookingsResponseModel().apply {
                val jsonObject = JSONObject(json)
                jsonObject.optJSONArray("data")?.forEach { items ->
                    workouts.add(WorkoutModel.parse(items))
                }
            }
        }
    }
}
