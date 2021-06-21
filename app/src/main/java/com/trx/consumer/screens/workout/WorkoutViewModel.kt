package com.trx.consumer.screens.workout

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.BookingsResponseModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.models.states.BookingViewState
import com.trx.consumer.models.states.WorkoutViewState
import kotlinx.coroutines.launch

class WorkoutViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    var model: WorkoutModel = WorkoutModel()

    val eventLoadView = CommonLiveEvent<WorkoutModel>()
    var eventLoadWorkoutView = CommonLiveEvent<WorkoutModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventShowPermissionAlert = CommonLiveEvent<Void>()

    val eventTapBack = CommonLiveEvent<Void>()
    var eventTapBookLive = CommonLiveEvent<WorkoutModel>()
    var eventTapProfile = CommonLiveEvent<TrainerModel>()
    var eventTapStartWorkout = CommonLiveEvent<WorkoutModel>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        analyticsManager.trackPageView(pageTitle)
        if (model.workoutState == WorkoutViewState.VIDEO) {
            doLoadVideo()
        } else {
            doLoadWorkout()
        }
    }

    private fun doLoadVideo() {
        eventLoadView.postValue(model)
    }

    private fun doLoadWorkout() {
        if (model.state != BookingState.BOOKED) {
            viewModelScope.launch {
                eventShowHud.postValue(true)
                val response = backendManager.bookings()
                eventShowHud.postValue(false)
                if (response.isSuccess) {
                    val bookingModel = BookingsResponseModel.parse(response.responseString)
                    bookingModel.lstWorkoutsSorted.firstOrNull { it == model }?.let { booking ->
                        model.state = BookingState.BOOKED
                        model.cancelId = booking.cancelId
                    }
                    eventLoadWorkoutView.postValue(model)
                }
            }
        } else {
            eventLoadWorkoutView.postValue(model)
        }
    }

    fun doTapProfile() {
        when (model.workoutState) {
            WorkoutViewState.VIDEO -> eventTapProfile.postValue(model.video.trainer)
            else -> eventTapProfile.postValue(model.trainer)
        }
    }

    fun doTapPrimary() {
        viewModelScope.launch {
            val user = cacheManager.user() ?: return@launch
            when (model.workoutState) {
                WorkoutViewState.VIDEO -> {
                    if (user.entitlements.onDemand) {
                        eventTapStartWorkout.postValue(model)
                    } else {
                        eventShowPermissionAlert.call()
                    }
                }
                WorkoutViewState.LIVE, WorkoutViewState.VIRTUAL -> {
                    if (model.bookViewStatus == BookingViewState.JOIN) {
                        eventTapStartWorkout.postValue(model)
                    } else {
                        user.card?.let {
                            eventTapBookLive.postValue(model)
                        } ?: run {
                            backendManager.user().let {
                                eventTapBookLive.postValue(model)
                            }
                        }
                    }
                }
                else -> LogManager.log("WorkoutViewModel.doTapPrimary")
            }
        }
    }
}
