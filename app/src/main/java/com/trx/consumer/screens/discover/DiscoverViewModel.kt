package com.trx.consumer.screens.discover

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.models.responses.VideosResponseModel
import com.trx.consumer.screens.discover.discoverfilter.DiscoverFilterListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import kotlinx.coroutines.launch

class DiscoverViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), VideoWorkoutListener, DiscoverFilterListener {

    //region Objects
    var workouts: List<VideoModel> = listOf()
    var collections: List<VideosModel> = listOf()
    var programs: List<VideosModel> = listOf()
    var params: FilterParamsModel = FilterParamsModel()
    var filters: List<FilterModel> = listOf()
    // endregion

    //region Variables
    val eventLoadWorkouts = CommonLiveEvent<List<VideoModel>>()
    val eventLoadCollections = CommonLiveEvent<List<VideosModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<VideosModel>>()
    val eventLoadFilters = CommonLiveEvent<List<FilterModel>>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapVideos = CommonLiveEvent<VideosModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()
    val eventTapDiscoverFilter = CommonLiveEvent<FilterParamsModel>()

    //endregion

    //region Actions
    fun doLoadView() {
        doLoadSkeletons()
        doLoadVideos()
    }

    private fun doLoadVideos() {
        filters = params.lstFilters
        val paramsToSend = params.params
        viewModelScope.launch {
            val response = backendManager.videos()
            if (response.isSuccess) {
                val model = VideosResponseModel.parse(response.responseString)
                workouts = model.workouts
                collections = model.collections
                programs = model.programs
                if (filters.isEmpty()) {
                    filters = model.filters.filter {
                        it.identifier.isNotEmpty() && it.values.isNotEmpty()
                    }
                }
            }
            params.lstFilters = filters
            eventLoadFilters.postValue(filters)
            if (paramsToSend.keys.any()) doLoadFilteredWorkouts(paramsToSend) else doLoadWorkouts()
        }
    }

    private suspend fun doLoadFilteredWorkouts(paramsToSend: HashMap<String, Any>) {
        val response = backendManager.videos(paramsToSend)
        if (response.isSuccess) {
            val model = VideosResponseModel.parse(response.responseString)
            eventLoadWorkouts.postValue(model.results)
        } else doLoadWorkouts()
    }

    fun doLoadWorkouts() {
        eventLoadFilters.postValue(filters)
        eventLoadWorkouts.postValue(workouts)
    }

    fun doLoadCollections() {
        resetFilters()
        eventLoadCollections.postValue(collections)
    }

    fun doLoadPrograms() {
        resetFilters()
        eventLoadPrograms.postValue(programs)
    }

    private fun doLoadSkeletons() {
        workouts = VideoModel.skeletonList(5)
        collections = VideosModel.skeletonList(5)
        programs = VideosModel.skeletonList(5)
        eventLoadWorkouts.postValue(workouts)
    }

    private fun resetFilters() {
        filters.forEach { it.values.forEach { model -> model.isSelected = false } }
        eventLoadFilters.postValue(filters)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapFilter() {
        eventTapFilter.postValue(params)
    }

    override fun doTapVideo(model: VideoModel) {
        eventTapVideo.postValue(model)
    }

    override fun doTapVideos(model: VideosModel) {
        eventTapVideos.postValue(model)
    }

    override fun doTapDiscoverFilter(filter: FilterModel) {
        params.selectedModel = filter
        eventTapDiscoverFilter.postValue(params)
    }
    //endregion
}
