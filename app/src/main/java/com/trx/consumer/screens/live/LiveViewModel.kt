package com.trx.consumer.screens.live

import androidx.lifecycle.viewModelScope
import com.trx.consumer.BuildConfig.isVersion2Enabled
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.BannerModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutCellViewState
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.BannerResponseModel
import com.trx.consumer.models.responses.BookingsResponseModel
import com.trx.consumer.models.responses.PromosResponseModel
import com.trx.consumer.models.responses.SessionsResponseModel
import com.trx.consumer.models.responses.TrainersResponseModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.promotion.PromoViewListener
import com.trx.consumer.screens.trainerprofile.TrainerProfileListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(),
    LiveWorkoutViewListener,
    TrainerProfileListener,
    PromoViewListener {

    //region variables

    private var bannerUrlString: String = ""

    //endregion

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()

    val eventLoadBanner = CommonLiveEvent<BannerModel>()
    val eventLoadPromos = CommonLiveEvent<List<PromoModel>>()
    val eventLoadTrainers = CommonLiveEvent<List<TrainerModel>>()
    val eventLoadWorkoutsToday = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadWorkoutsTomorrow = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadWorkoutsRecommended = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadWorkoutsUpcoming = CommonLiveEvent<List<WorkoutModel>>()

    val eventShowBooking = CommonLiveEvent<WorkoutModel>()
    val eventShowPromo = CommonLiveEvent<PromoModel>()
    val eventShowTrainer = CommonLiveEvent<TrainerModel>()
    val eventShowWorkout = CommonLiveEvent<WorkoutModel>()
    val eventShowVirtualWorkout = CommonLiveEvent<WorkoutModel>()
    val eventLoadViewAllUpcoming = CommonLiveEvent<Void>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapBanner = CommonLiveEvent<String>()
    val eventTapScheduleToday = CommonLiveEvent<Void>()
    val eventTapScheduleTomorrow = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()
    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
        if (isVersion2Enabled) {
            doLoadPromotions()
            doLoadSessions()
            doLoadTrainers()
            doLoadWorkoutsUpcoming()
        } else {
            doLoadBanner()
        }
    }

    private fun doLoadBanner() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.banner()
            if (response.isSuccess) {
                val model = BannerResponseModel.parse(response.responseString)
                model.banner.let { banner ->
                    eventLoadBanner.postValue(banner)
                    bannerUrlString = banner.offerButtonLink
                }
            }
            eventShowHud.postValue(false)
        }
    }

    private fun doLoadPromotions() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.promos()
            if (response.isSuccess) {
                val model = PromosResponseModel.parse(response.responseString)
                eventLoadPromos.postValue(model.promos)
            }
            eventShowHud.postValue(false)
        }
    }

    private fun doLoadSessions() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.workouts()
            if (response.isSuccess) {
                val model = SessionsResponseModel.parse(response.responseString)
                eventLoadWorkoutsToday.postValue(model.listToday)
                eventLoadWorkoutsTomorrow.postValue(model.listTomorrow)
                eventLoadWorkoutsRecommended.postValue(model.listNextWeek)
            }
            eventShowHud.postValue(false)
        }
    }

    private fun doLoadTrainers() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.trainers()
            if (response.isSuccess) {
                val model = TrainersResponseModel.parse(response.responseString)
                eventLoadTrainers.postValue(model.listLiveTrainers)
            }
            eventShowHud.postValue(false)
        }
    }

    private fun doLoadWorkoutsUpcoming() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.bookings()
            if (response.isSuccess) {
                val model = BookingsResponseModel.parse(response.responseString)
                eventLoadWorkoutsUpcoming.postValue(model.lstLiveUpcoming)
            }
            eventShowHud.postValue(false)
        }
    }

    fun doTapBanner() {
        eventTapBanner.postValue(bannerUrlString)
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {
        if (model.cellViewStatus == WorkoutCellViewState.BOOKED) {
            eventShowBooking.postValue(model)
        } else {
            doTapSelectLiveWorkout(model)
        }
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
        eventShowWorkout.postValue(model)
    }

    override fun doTapTrainerProfile(model: TrainerModel) {
        eventShowTrainer.postValue(model)
    }

    override fun doTapPromo(model: PromoModel) {
        eventShowPromo.postValue(model)
    }

    fun doTapScheduleToday() {
        eventTapScheduleToday.call()
    }

    fun doTapScheduleTomorrow() {
        eventTapScheduleTomorrow.call()
    }

    fun doLoadViewAllUpcoming() {
        eventLoadViewAllUpcoming.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    //endregion
}
