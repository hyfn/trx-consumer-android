package com.trx.consumer.screens.virtual

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
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
    private val backendManager: BackendManager
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

    fun doLoadView() {
        eventLoadView.call()
        doLoadBookings()
        doLoadTrainers()
        doLoadPromos()
    }

    private fun doLoadBookings() {
        viewModelScope.launch {
            val response = backendManager.bookings()
            eventLoadBookWith.postValue(trainer)
            if (response.isSuccess) {
                val model = BookingsResponseModel.parse(response.responseString)
                val virtualWorkouts = model.lstVirtualUpcoming
                val loadMatchMe = virtualWorkouts.isEmpty()

                eventLoadMatchMe.postValue(loadMatchMe)
                eventLoadWorkouts.postValue(virtualWorkouts)

                trainer = virtualWorkouts.firstOrNull()?.trainer
                eventLoadBookWith.postValue(trainer)

                if (loadMatchMe) {
                    eventLoadCalendar.postValue(null)
                } else {
                    eventLoadCalendar.postValue(model.calendarModelVirtual)
                }
            }
        }
    }

    private fun doLoadPromos() {
        viewModelScope.launch {
            val response = backendManager.promos()
            if (response.isSuccess) {
                val model = PromosResponseModel.parse(response.responseString)
                eventLoadPromos.postValue(model.promos)
            }
        }
    }

    private fun doLoadTrainers() {
        viewModelScope.launch {
            val response = backendManager.trainers()
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
        trainer?.let { safeTrainer ->
            eventShowTrainer.postValue(safeTrainer)
        }
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
