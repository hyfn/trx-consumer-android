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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), VideoWorkoutListener, DiscoverFilterListener {

    var workouts: List<VideoModel> = listOf()
    var collections: List<VideosModel> = listOf()
    var programs: List<VideosModel> = listOf()
    var params: FilterParamsModel = FilterParamsModel()
    var filters: List<FilterModel> = listOf()
    var isFilterEnable: Boolean = true

    val eventLoadWorkouts = CommonLiveEvent<List<VideoModel>>()
    val eventLoadCollections = CommonLiveEvent<List<VideosModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<VideosModel>>()
    val eventLoadFilters = CommonLiveEvent<List<FilterModel>>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapVideos = CommonLiveEvent<VideosModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()
    val eventTapDiscoverFilter = CommonLiveEvent<FilterParamsModel>()

    fun doLoadVideos() {
        filters = params.lstFilters
        val paramsToSend = params.params
        viewModelScope.launch {
            doLoadSkeletons()
            withContext(Dispatchers.IO) { delay(10000) }
            val response = backendManager.videos()
            if (response.isSuccess) {
                val model = VideosResponseModel.parse(response.responseString)
                workouts =
                    if (paramsToSend.keys.any()) doLoadFilteredWorkout(paramsToSend) else model.workouts
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
            doLoadWorkouts()
        }
    }

    private suspend fun doLoadFilteredWorkout(paramsToSend: HashMap<String, Any>): List<VideoModel> {
        val response = backendManager.videos(paramsToSend)
        return if (response.isSuccess) {
            val model = VideosResponseModel.parse(response.responseString)
            model.results
        } else listOf()
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
        doLoadWorkouts()
    }

    private fun resetFilters() {
        filters.forEach { it.values.forEach { model -> model.isSelected = false } }
        eventLoadFilters.postValue(filters)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapFilter() {
        if (isFilterEnable) eventTapFilter.postValue(params)
    }

    override fun doTapVideo(model: VideoModel) {
        eventTapVideo.postValue(model)
    }

    override fun doTapVideos(model: VideosModel) {
        eventTapVideos.postValue(model)
    }

    override fun doTapDiscoverFilter(filter: FilterModel) {
        params.selectedModel = filter
        if (isFilterEnable) eventTapDiscoverFilter.postValue(params)
    }

    fun setFilterClick(isClickable: Boolean) {
        isFilterEnable = isClickable
    }
}
