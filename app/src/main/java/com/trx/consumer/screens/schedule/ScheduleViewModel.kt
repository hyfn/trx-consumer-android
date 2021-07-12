package com.trx.consumer.screens.schedule

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.isSameDay
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel
import com.trx.consumer.models.common.CalendarModel
import com.trx.consumer.models.common.DaysModel
import com.trx.consumer.models.common.TrainerScheduleModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.BookingsResponseModel
import com.trx.consumer.models.responses.SessionsResponseModel
import com.trx.consumer.models.states.ScheduleViewState
import com.trx.consumer.models.states.ScheduleViewState.LIVE
import com.trx.consumer.models.states.ScheduleViewState.TRAINER_LIVE
import com.trx.consumer.models.states.ScheduleViewState.TRAINER_VIRTUAL
import com.trx.consumer.models.states.ScheduleViewState.USER_LIVE
import com.trx.consumer.models.states.ScheduleViewState.USER_VIRTUAL
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.trainerschedule.TrainerScheduleListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener
import com.trx.consumer.views.calendar.days.DaysTapListener
import kotlinx.coroutines.launch
import java.util.Date

class ScheduleViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(),
    LiveWorkoutViewListener,
    VirtualWorkoutViewListener,
    TrainerScheduleListener,
    DaysTapListener {

    var key: String? = null
    var date = Date()
    var state: ScheduleViewState = LIVE
    var model: SessionsResponseModel? = null

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadCalendarView = CommonLiveEvent<CalendarModel>()
    val eventLoadLiveWorkouts = CommonLiveEvent<List<WorkoutModel>>()

    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.call()
        when (state) {
            LIVE -> doLoadLiveWorkouts()
            USER_LIVE -> doLoadLiveBookings()
            TRAINER_LIVE -> doLoadTrainerLive()
            else -> {
            }
        }
    }

    private fun doLoadLiveBookings() {
        viewModelScope.launch {
            val response = backendManager.bookings()
            if (response.isSuccess) {
                val model = BookingsResponseModel.parse(response.responseString)
                eventLoadLiveWorkouts.postValue(model.lstLiveUpcoming)
                eventLoadCalendarView.postValue(model.calendarModelVirtual)
            }
        }
    }

    private fun doLoadLiveWorkouts() {
        viewModelScope.launch {
            val response = backendManager.workouts()
            if (response.isSuccess) {
                model = SessionsResponseModel.parse(response.responseString)
                loadLive(Date())
            }
        }
    }

    private fun doLoadTrainerLive() {
        key?.let { safeKey ->
            viewModelScope.launch {
                val response = backendManager.trainerSessions(safeKey)
                if (response.isSuccess) {
                    model = SessionsResponseModel.parse(response.responseString)
                    loadLive(Date())
                }
            }
        }
    }

    private fun loadLive(date: Date) {
        model?.let { safeModel ->
            eventLoadCalendarView.postValue(
                CalendarModel().apply {
                    selectedDate = date
                    listEvents = safeModel.listUpcoming.map { DaysModel(it.date) }
                }
            )
        }
        doTapDate(Date())
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
    }

    override fun doTapPrimaryVirtualWorkout(model: WorkoutModel) {
    }

    override fun doTapSelectVirtualWorkout(model: WorkoutModel) {
    }

    override fun doTapClass(trainerScheduleModel: TrainerScheduleModel) {
    }

    override fun doTapDate(date: Date) {
        when (state) {
            LIVE, TRAINER_LIVE -> {
                model?.listUpcoming?.filter { it.date.isSameDay(date) }?.let { workouts ->
                    eventLoadLiveWorkouts.postValue(workouts)
                }
            }

            USER_LIVE -> {
                LogManager.log("ScheduleViewModel.doTapDate $date")
            }
            else -> { }
        }
    }

    fun doTrackPageView() {
        when (state) {
            TRAINER_LIVE -> { analyticsManager.trackPageView(AnalyticsPageModel.SCHEDULE_TRAINER_LIVE) }
            TRAINER_VIRTUAL -> { analyticsManager.trackPageView(AnalyticsPageModel.SCHEDULE_TRAINER_VIRTUAL) }
            USER_LIVE -> { analyticsManager.trackPageView(AnalyticsPageModel.SCHEDULE_USER_LIVE) }
            USER_VIRTUAL -> { analyticsManager.trackPageView(AnalyticsPageModel.SCHEDULE_USER_VIRTUAL) }
            else -> { LogManager.log("Invalid state: ${state.name}") }
        }
    }
}
