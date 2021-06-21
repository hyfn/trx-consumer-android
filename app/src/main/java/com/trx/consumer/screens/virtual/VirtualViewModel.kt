package com.trx.consumer.screens.virtual

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.CalendarModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.BookingsResponseModel
import com.trx.consumer.models.responses.PromosResponseModel
import com.trx.consumer.models.responses.TrainersResponseModel
import com.trx.consumer.screens.promotion.PromoViewListener
import com.trx.consumer.screens.trainerprofile.TrainerProfileListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener
import kotlinx.coroutines.launch

class VirtualViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(),
    PromoViewListener,
    TrainerProfileListener,
    VirtualWorkoutViewListener {

    var trainer: TrainerModel? = null

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()

    val eventLoadBookWith = CommonLiveEvent<TrainerModel?>()
    val eventLoadCalendar = CommonLiveEvent<CalendarModel?>()
    val eventLoadMatchMe = CommonLiveEvent<Boolean>()
    val eventLoadPromos = CommonLiveEvent<List<PromoModel>>()
    val eventLoadTrainers = CommonLiveEvent<List<TrainerModel>>()
    val eventLoadWorkouts = CommonLiveEvent<List<WorkoutModel>>()

    val eventShowMatchMe = CommonLiveEvent<Void>()
    val eventShowPromo = CommonLiveEvent<PromoModel>()

    val eventShowTrainer = CommonLiveEvent<TrainerModel>()
    val eventShowTrainerSchedule = CommonLiveEvent<TrainerModel>()
    val eventShowUserSchedule = CommonLiveEvent<Void>()
    val eventShowWorkout = CommonLiveEvent<WorkoutModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
        doLoadBookings()
        doLoadTrainers()
        doLoadPromos()
    }

    private fun doLoadBookings() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.bookings()
            eventShowHud.postValue(false)
            eventLoadBookWith.postValue(trainer)
            if (response.isSuccess) {
                val model = BookingsResponseModel.parse(response.responseString)
                val virtualWorkouts = model.lstVirtualUpcoming
                val loadMatchMe = virtualWorkouts.isNotEmpty()

                eventLoadMatchMe.postValue(loadMatchMe)
                eventLoadWorkouts.postValue(virtualWorkouts)

                trainer = virtualWorkouts.firstOrNull()?.trainer
                eventLoadBookWith.postValue(trainer)

                if (loadMatchMe) {
                    eventLoadCalendar.postValue(model.calendarModelVirtual)
                } else {
                    eventLoadCalendar.postValue(null)
                }
            }
        }
    }

    private fun doLoadPromos() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.promos()
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                val model = PromosResponseModel.parse(response.responseString)
                eventLoadPromos.postValue(model.promos)
            }
        }
    }

    private fun doLoadTrainers() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.trainers()
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                val model = TrainersResponseModel.parse(response.responseString)
                eventLoadTrainers.postValue(model.listFeaturedTrainers)
            }
        }
    }

    fun doTapMatchMe() {
        eventShowMatchMe.call()
    }

    fun doTapSchedule() {
        eventShowUserSchedule.call()
    }

    fun doTapViewTrainerProfile() {
        trainer?.let { safeTrainer -> eventShowTrainer.postValue(safeTrainer) }
    }

    fun doTapViewAllBookings() {
        eventShowUserSchedule.call()
    }

    override fun doTapPromo(model: PromoModel) {
        eventShowPromo.postValue(model)
    }

    override fun doTapTrainerProfile(model: TrainerModel) {
        eventShowTrainer.postValue(model)
    }

    override fun doTapPrimaryVirtualWorkout(model: WorkoutModel) {
        eventShowWorkout.postValue(model)
    }

    override fun doTapSelectVirtualWorkout(model: WorkoutModel) {
        eventShowWorkout.postValue(model)
    }

    //endregion
}
