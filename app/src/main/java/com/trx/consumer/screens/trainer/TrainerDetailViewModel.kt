package com.trx.consumer.screens.trainer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.TrainerProgramModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutCellViewState
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.ProgramsResponseModel
import com.trx.consumer.models.responses.SessionsResponseModel
import com.trx.consumer.models.responses.TrainerResponseModel
import com.trx.consumer.models.responses.VideosResponseModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import kotlinx.coroutines.launch

class TrainerDetailViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), LiveWorkoutViewListener, VideoWorkoutListener {


    var trainer: TrainerModel = TrainerModel()
    var photos: List<String> = listOf()

    // MARK>
    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadWorkoutUpcoming = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadServices = CommonLiveEvent<List<TrainerProgramModel>>()
    val eventLoadBanner = CommonLiveEvent<List<String>>()
    val eventLoadPhotos = CommonLiveEvent<List<String>>()
    val eventLoadOndemand = CommonLiveEvent<List<VideoModel>>()
    val eventLoadTrainer = CommonLiveEvent<TrainerModel>()
    val eventShowBanner = CommonLiveEvent<String>()
    val eventShowBooking = CommonLiveEvent<WorkoutModel>()
    val eventShowOndemand = CommonLiveEvent<VideoModel>()
    val eventShowOndemandSeeAll = CommonLiveEvent<Void>()
    val eventShowPhoto = CommonLiveEvent<String>()
    val eventShowRecommendationsForyou = CommonLiveEvent<Void>()
    val eventShowService = CommonLiveEvent<TrainerProgramModel>()
    val eventShowUpcomingSchedule = CommonLiveEvent<String>()
    val eventShowWorkout = CommonLiveEvent<WorkoutModel>()
    val eventTapAboutMe = CommonLiveEvent<ContentModel>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.call()
        doLoadData()
        doLoadOndemand()
        doLoadWorkoutsUpcoming()
        doLoadTrainerServices()
    }

    fun doLoadData() {
        viewModelScope.launch {
            val response = backendManager.trainer(trainer.key)
            if (response.isSuccess) {
                val model = TrainerResponseModel.parse(response.responseString)
                eventLoadTrainer.postValue(model.trainer)
                eventLoadBanner.postValue(model.trainer.lstBadgeUrls)
            }
        }
    }

    fun doTapRecommendationsForyou() {
        eventShowRecommendationsForyou.call()
    }

    // MARK: - VirtualWorkoutViewListener
    fun doTapUpcomingSchedule() {
        eventShowUpcomingSchedule.postValue(trainer.key)
    }

    fun doLoadWorkoutsUpcoming() {
        viewModelScope.launch {
            val response = backendManager.trainerSessions(trainer.key)
            if (response.isSuccess) {
                val model = SessionsResponseModel.parse(response.responseString)
                eventLoadWorkoutUpcoming.postValue(model.listUpcoming)
            }
        }
    }

    // BannerViewListener
    fun doTapBannerPrimaryPhotos(model: String) {
        eventShowBanner.postValue(model)
    }

    // VideoWorkoutViewListener
    fun doLoadOndemand() {
        viewModelScope.launch {
            val response = backendManager.videos()
            if (response.isSuccess) {
                val model = VideosResponseModel.parse(response.responseString)
                eventLoadOndemand.postValue(model.lstWorkoutsForTrainerProfileId(trainer.virtualTrainerProfileId))

            }
        }
    }

    fun doTapOndemandSeeAll() {
        eventShowOndemandSeeAll.call()
    }

    fun doTapAboutMe() {
        eventTapAboutMe.postValue(trainer.contentModel)
    }

    override fun doTapVideo(model: VideoModel) {
        eventShowOndemand.postValue(model)
    }

    // PhotosViewListener
    fun doLoadPhotos() {
        eventLoadPhotos.postValue(photos)
    }

    fun doTapSelectPhotos(model: String) {
        eventShowPhoto.postValue(model)
    }

    // TrainerProgramViewListener
    fun doLoadTrainerServices() {
        viewModelScope.launch {
            val response = backendManager.trainerPrograms(trainer.key)
            if (response.isSuccess) {
                val model = ProgramsResponseModel.parse(response.responseString)
                eventLoadServices.postValue(model.lstPrograms)
            }
        }
    }

    fun doTapProgram(model: TrainerProgramModel) {
        eventShowService.postValue(model)
    }

    // Live listener
    override fun doTapBookLiveWorkout(model: WorkoutModel) {
        if (model.cellViewStatus == WorkoutCellViewState.BOOKED)
            eventShowBooking.postValue(model)
        else doTapSelectLiveWorkout(model)
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
        eventShowWorkout.postValue(model)
    }
}
