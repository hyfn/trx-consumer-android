package com.trx.consumer.screens.testutility

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.SessionsResponseModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.promotion.PromoViewListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener
import kotlinx.coroutines.launch

class TestUtilityViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(),
    LiveWorkoutViewListener,
    VirtualWorkoutViewListener,
    VideoWorkoutListener,
    PromoViewListener {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapCards = CommonLiveEvent<Void>()
    val eventTapAddCard = CommonLiveEvent<Void>()
    val eventTapUpdate = CommonLiveEvent<Void>()
    val eventTapContent = CommonLiveEvent<Void>()
    val eventTapFilter = CommonLiveEvent<Void>()
    val eventTapVideo = CommonLiveEvent<Void>()
    val eventTapDiscover = CommonLiveEvent<Void>()
    val eventTapAlert = CommonLiveEvent<Void>()
    val eventTapWelcome = CommonLiveEvent<Void>()
    val eventTapSettings = CommonLiveEvent<Void>()
    val eventTapWorkout = CommonLiveEvent<Void>()
    val eventTapWorkoutLive = CommonLiveEvent<Void>()
    val eventTapTrainer = CommonLiveEvent<Void>()
    val eventTapBookingAlert = CommonLiveEvent<Void>()
    val eventTapSchedule = CommonLiveEvent<Void>()
    val eventTapMemberships = CommonLiveEvent<Void>()
    val eventLoadLiveWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadVirtualWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadVideoWorkouts = CommonLiveEvent<List<VideoModel>>()
    val eventLoadPromotions = CommonLiveEvent<List<PromoModel>>()
    val eventLoadingScreen = CommonLiveEvent<Void>()
    val eventLoadGroupPlayer = CommonLiveEvent<Void>()
    val eventPrivatePlayer = CommonLiveEvent<Void>()

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
                eventLoadLiveWorkouts.postValue(WorkoutModel.testListLive(5))
            }
        }
    }

    private fun doLoadVirtualWorkouts() {
        eventLoadVirtualWorkouts.postValue(WorkoutModel.testListLive(5))
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

    fun doTapVideo() {
        eventTapVideo.call()
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

    fun doTapWorkoutLive() {
        eventTapWorkoutLive.call()
    }

    fun doTapTrainer() {
        eventTapTrainer.call()
    }

    fun doTapBookingAlert() {
        eventTapBookingAlert.call()
    }

    fun doTapLoadingScreen() {
        eventLoadingScreen.call()
    }

    fun doTapBook(model: WorkoutModel) {}

    fun doTapSchedule() {
        eventTapSchedule.call()
    }

    fun doTapMemberships() {
        eventTapMemberships.call()
    }

    fun doTapGroupPlayer() {
        eventLoadGroupPlayer.call()
    }

    fun doTapPrivatePlayer() {
        eventPrivatePlayer.call()
    }

    override fun doTapBookLiveWorkout(model: WorkoutModel) {}

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {}

    override fun doTapPrimaryVirtualWorkout(model: WorkoutModel) {}

    override fun doTapSelectVirtualWorkout(model: WorkoutModel) {}

    override fun doTapVideo(model: VideoModel) {}

    override fun doTapPromo(model: PromoModel) {}

    //endregion
}
