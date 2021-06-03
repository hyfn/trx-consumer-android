package com.trx.consumer.screens.workout

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.BuildConfig.isVersion1Enabled
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.IAPManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.BookingsResponseModel
import com.trx.consumer.models.responses.PurchasesResponseModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.models.states.BookingViewState
import com.trx.consumer.models.states.WorkoutViewState
import kotlinx.coroutines.launch

class WorkoutViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel() {

    var model: WorkoutModel = WorkoutModel()

    val eventLoadView = CommonLiveEvent<WorkoutModel>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadSubscribePrompt = CommonLiveEvent<Void>()
    var eventLoadWorkoutView = CommonLiveEvent<WorkoutModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()

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
        when (model.workoutState) {
            WorkoutViewState.VIDEO -> {
                if (!isVersion1Enabled) {
                    eventTapStartWorkout.postValue(model)
                    return
                }
                doTapVideo()
            }
            WorkoutViewState.LIVE, WorkoutViewState.VIRTUAL -> doTapLiveOrVirtual()
            else -> LogManager.log("WorkoutViewModel.doTapPrimary")
        }
    }

    private fun doTapVideo() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.purchases()
            if (response.isSuccess) {
                val responseModel = PurchasesResponseModel.parse(response.responseString)
                val packages = IAPManager.shared.packages()
                responseModel.onDemandSubscription()?.let { entitlement ->
                    packages.find {
                        it.iapPackage.product.sku == entitlement.productIdentifier
                    }?.let { match ->
                        LogManager.log("Matched purchased package ${match.iapPackage.product.sku}")
                        eventTapStartWorkout.postValue(model)
                    } ?: eventLoadError.postValue("No matching package")
                } ?: eventLoadSubscribePrompt.call()
            } else {
                eventLoadError.postValue("Could not load purchases")
            }
        }.invokeOnCompletion { eventShowHud.postValue(false) }
    }

    private fun doTapLiveOrVirtual() {
        viewModelScope.launch {
            if (model.bookViewStatus == BookingViewState.JOIN) {
                eventTapStartWorkout.postValue(model)
            } else {
                cacheManager.user()?.card?.let {
                    eventTapBookLive.postValue(model)
                } ?: backendManager.user().let {
                    eventTapBookLive.postValue(model)
                }
            }
        }
    }
}
