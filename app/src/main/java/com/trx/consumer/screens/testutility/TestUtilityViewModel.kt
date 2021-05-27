package com.trx.consumer.screens.testutility

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.SessionsResponseModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.promotion.PromotionListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutListener
import kotlinx.coroutines.launch

class TestUtilityViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(),
    LiveWorkoutListener,
    VirtualWorkoutListener,
    VideoWorkoutListener,
    PromotionListener {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapCards = CommonLiveEvent<Void>()
    val eventTapAddCard = CommonLiveEvent<Void>()
    val eventTapUpdate = CommonLiveEvent<Void>()
    val eventTapContent = CommonLiveEvent<Void>()
    val eventTapPlans = CommonLiveEvent<Void>()
    val eventTapFilter = CommonLiveEvent<Void>()
    val eventTapPlayer = CommonLiveEvent<Void>()
    val eventTapDiscover = CommonLiveEvent<Void>()
    val eventTapAlert = CommonLiveEvent<Void>()
    val eventTapWelcome = CommonLiveEvent<Void>()
    val eventTapSettings = CommonLiveEvent<Void>()
    val eventTapWorkout = CommonLiveEvent<Void>()
    val eventTapTrainer = CommonLiveEvent<Void>()
    val eventTapBookingAlert = CommonLiveEvent<Void>()
    val eventLoadLiveWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadVirtualWorkouts = CommonLiveEvent<List<VirtualWorkoutModel>>()
    val eventLoadVideoWorkouts = CommonLiveEvent<List<VideoModel>>()
    val eventLoadPromotions = CommonLiveEvent<List<PromoModel>>()

    //endregion

    //region Functions

    fun doLoadView() {
        doLoadLiveWorkouts()
        doLoadVirtualWorkouts()
        doLoadVideoWorkouts()
        doLoadPromotions()
    }

    private fun doLoadLiveWorkouts() {
        viewModelScope.launch {
            val response = backendManager.workouts()
            if (response.isSuccess) {
                val responseModel = SessionsResponseModel.parse(response.responseString)
                eventLoadLiveWorkouts.postValue(responseModel.sessions.take(5))
            } else {
                eventLoadLiveWorkouts.postValue(WorkoutModel.testList(5))
            }
        }
    }

    private fun doLoadVirtualWorkouts() {
        eventLoadVirtualWorkouts.postValue(VirtualWorkoutModel.testListVariety())
    }

    private fun doLoadVideoWorkouts() {
        eventLoadVideoWorkouts.postValue(VideoModel.testList(5))
    }

    private fun doLoadPromotions() {
        eventLoadPromotions.postValue(PromoModel.testList(5))
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapCards() {
        eventTapCards.call()
    }

    fun doTapAddCard() {
        eventTapAddCard.call()
    }

    fun doTapUpdate() {
        eventTapUpdate.call()
    }

    fun doTapContent() {
        eventTapContent.call()
    }

    fun doTapPlans() {
        eventTapPlans.call()
    }

    fun doTapPlayer() {
        eventTapPlayer.call()
    }

    fun doTapDiscover() {
        eventTapDiscover.call()
    }

    fun doTapAlert() {
        eventTapAlert.call()
    }

    fun doTapFilter() {
        eventTapFilter.call()
    }

    fun doTapWelcome() {
        eventTapWelcome.call()
    }

    fun doTapSettings() {
        eventTapSettings.call()
    }

    fun doTapWorkout() {
        eventTapWorkout.call()
    }

    fun doTapTrainer() {
        eventTapTrainer.call()
    }

    fun doTapBookingAlert() {
        eventTapBookingAlert.call()
    }

    fun doTapBook(model: WorkoutModel) {}

    override fun doTapBookLiveWorkout(model: WorkoutModel) {}

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {}

    override fun doTapPrimary(model: VirtualWorkoutModel) {}

    override fun doTapSelect(model: VirtualWorkoutModel) {}

    override fun doTapVideo(model: VideoModel) {}

    override fun doTapPromo(model: PromoModel) {}

    //endregion
}
