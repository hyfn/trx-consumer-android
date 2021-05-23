package com.trx.consumer.models.responses

import com.trx.consumer.BuildConfig.kMinutesAfterCanJoin
import com.trx.consumer.extensions.elapsedMin
import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.common.WorkoutViewState
import org.json.JSONObject
import java.util.Date
import java.util.concurrent.TimeUnit

class BookingsResponseModel(private val workouts: List<WorkoutModel>) {

    val listWorkoutsSorted: List<WorkoutModel>
        get() {
            return workouts.filter { !it.isCanceled }.sortedBy { it.startsAt }.map { it.booking }
        }

    val listLiveUpcoming: List<WorkoutModel>
        get() {
            return listWorkoutsSorted.filter {
                it.workoutState == WorkoutViewState.LIVE &&
                        it.date.elapsedMin() <= kMinutesAfterCanJoin
            }
        }

    val listVirtualUpcoming: List<WorkoutModel>
        get() {
            return listWorkoutsSorted.filter {
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