package com.trx.consumer.screens.trainer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.BookingAlertModel
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
import com.trx.consumer.screens.banner.BannerViewListener
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.photos.PhotosViewListener
import com.trx.consumer.screens.trainerprogram.TrainerProgramViewListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import kotlinx.coroutines.launch

class TrainerDetailViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel(),
    LiveWorkoutViewListener,
    VideoWorkoutListener,
    BannerViewListener,
    TrainerProgramViewListener,
    PhotosViewListener {

    var trainer: TrainerModel = TrainerModel.test()
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
    val eventShowBooking = CommonLiveEvent<BookingAlertModel>()
    val eventShowOndemand = CommonLiveEvent<VideoModel>()
    val eventShowOndemandSeeAll = CommonLiveEvent<Void>()
    val eventShowPhoto = CommonLiveEvent<String>()
    val eventShowRecommendationsForyou = CommonLiveEvent<Void>()
    val eventShowService = CommonLiveEvent<TrainerProgramModel>()
    val eventShowUpcomingSchedule = CommonLiveEvent<String>()
    val eventShowWorkout = CommonLiveEvent<WorkoutModel>()
    val eventTapAboutMe = CommonLiveEvent<ContentModel>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.call()
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

    fun doLoadWorkoutsUpcoming() {
        viewModelScope.launch {
            val response = backendManager.trainerSessions(trainer.key)
            if (response.isSuccess) {
                val model = SessionsResponseModel.parse(response.responseString)
                eventLoadWorkoutUpcoming.postValue(model.listUpcoming)
            }
        }
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

    fun doLoadTrainerServices() {
        viewModelScope.launch {
            val response = backendManager.trainerPrograms(trainer.key)
            if (response.isSuccess) {
                val model = ProgramsResponseModel.parse(response.responseString)
                eventLoadServices.postValue(model.lstPrograms)
            }
        }
    }

    fun doLoadPhotos() {
        eventLoadPhotos.postValue(photos)
    }

    fun doTapBack() {
        eventTapBack.call()
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

    // VirtualWorkoutViewListener
    fun doTapUpcomingSchedule() {
        eventShowUpcomingSchedule.postValue(trainer.key)
    }

    // BannerViewListener
    override fun doTapBannerPrimaryPhotos(model: String) {
        eventShowBanner.postValue(model)
    }

    // PhotosViewListener
    override fun doTapSelectPhotos(model: String) {
        eventShowPhoto.postValue(model)
    }

    // TrainerProgramViewListener
    override fun doTapProgram(model: TrainerProgramModel) {
        eventShowService.postValue(model)
    }

    // Live listener
    override fun doTapBookLiveWorkout(model: WorkoutModel) {
        if (model.cellViewStatus == WorkoutCellViewState.BOOKED)
            viewModelScope.launch {
                cacheManager.user()?.card?.let { card ->
                    eventShowBooking.postValue(BookingAlertModel(card, model))
                }
            }
        else doTapSelectLiveWorkout(model)
    }

    override fun doTapSelectLiveWorkout(model: WorkoutModel) {
        eventShowWorkout.postValue(model)
    }
}
