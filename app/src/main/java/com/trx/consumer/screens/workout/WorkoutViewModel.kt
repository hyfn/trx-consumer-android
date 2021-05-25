package com.trx.consumer.screens.workout

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.BookingsResponseModel
import com.trx.consumer.models.states.BookingState
import kotlinx.coroutines.launch

class WorkoutViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel() {

    var model: WorkoutModel = WorkoutModel()

    val eventLoadView = CommonLiveEvent<WorkoutModel>()
    var eventLoadWorkoutView = CommonLiveEvent<WorkoutModel>()

    val eventTapBack = CommonLiveEvent<Void>()
    var eventTapBookLive = CommonLiveEvent<WorkoutModel>()
    var eventTapProfile = CommonLiveEvent<TrainerModel>()
    var eventTapStartWorkout = CommonLiveEvent<WorkoutModel>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
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
                val response = backendManager.bookings()
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
        when (model.workoutState) {
            WorkoutViewState.VIDEO -> eventTapStartWorkout.postValue(model)
            WorkoutViewState.LIVE, WorkoutViewState.VIRTUAL -> {
                if (model.bookViewStatus == BookingState.JOIN) {
                    eventTapStartWorkout.postValue(model)
                } else {
                    viewModelScope.launch {
                        cacheManager.user()?.card?.let {
                            eventTapBookLive.postValue(model)
                        } ?: run {
                            backendManager.user().let {
                                eventTapBookLive.postValue(model)
                            }
                        }
                    }
                }
            }
            else -> LogManager.log("WorkoutViewModel.doTapPrimary")
        }
    }
}
