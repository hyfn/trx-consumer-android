package com.trx.consumer.models.responses

import com.trx.consumer.extensions.isToday
import com.trx.consumer.extensions.isTomorrow
import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.WorkoutViewState
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Date

class SessionsResponseModel(val sessions: List<WorkoutModel>) {

    private val listSessionsSorted: List<WorkoutModel>
        get() = sessions.filter { it.workoutState == WorkoutViewState.LIVE }.sortedBy { it.date }

    val listNextWeek: List<WorkoutModel>
        get() {
            val date = sundayDate ?: return emptyList()
            return listSessionsSorted.filter { it.date.after(date) }
        }

    val listToday: List<WorkoutModel>
        get() = listSessionsSorted.filter {
            val date = it.date
            date.isToday() && date.after(Date())
        }

    val listTomorrow: List<WorkoutModel>
        get() = listSessionsSorted.filter { it.date.isTomorrow() }

    val listUpcoming: List<WorkoutModel>
        get() = listSessionsSorted.filter { it.date.after(Date()) }

    private val sundayDate: Date?
        get() {
            val date = LocalDate.now()
            val nextSunday = date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
            return Date.from(nextSunday.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

    companion object {
        fun parse(json: String): SessionsResponseModel {
            val jsonObject = JSONObject(json)
            val sessions = jsonObject.optJSONArray("data").map { WorkoutModel.parse(it) }
            return SessionsResponseModel(sessions)
        }
    }
}
