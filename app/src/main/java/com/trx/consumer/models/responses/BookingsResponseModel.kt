package com.trx.consumer.models.responses

import com.trx.consumer.BuildConfig.kMinutesAfterCanJoin
import com.trx.consumer.extensions.elapsedMin
import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.WorkoutViewState
import org.json.JSONObject
import java.util.Date

class BookingsResponseModel(val workouts: List<WorkoutModel>) {

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

    val lstLiveUpcoming: List<WorkoutModel>
        get() {
            return lstWorkoutsSorted.filter {
                it.workoutState == WorkoutViewState.LIVE &&
                    it.date.elapsedMin() < kMinutesAfterCanJoin
            }
        }

    val lstVirtualUpcoming: List<WorkoutModel>
        get() {
            return lstWorkoutsSorted.filter {
                it.workoutState == WorkoutViewState.VIRTUAL && it.date.after(Date())
            }
        }

    companion object {

        fun parse(json: String): BookingsResponseModel {
            val jsonObject = JSONObject(json)
            val workouts = jsonObject.optJSONArray("data").map { WorkoutModel.parse(it) }
            return BookingsResponseModel(workouts)
        }
    }
}
