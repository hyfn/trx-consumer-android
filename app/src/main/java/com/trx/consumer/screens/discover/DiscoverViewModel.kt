package com.trx.consumer.screens.discover

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.AnalyticsPageModel.DISCOVER
import com.trx.consumer.models.common.DiscoverModel
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.models.responses.VideosResponseModel
import com.trx.consumer.screens.discover.discoverfilter.DiscoverFilterListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import kotlinx.coroutines.launch

class DiscoverViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), VideoWorkoutListener, DiscoverFilterListener {

    //region Objects

    var model = DiscoverModel()
    var params: FilterParamsModel = FilterParamsModel()
    // endregion

    //region Events
    val eventLoadView = CommonLiveEvent<DiscoverModel>()
    val eventLoadFilters = CommonLiveEvent<DiscoverModel>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapVideo = CommonLiveEvent<VideoModel>()
    val eventTapVideos = CommonLiveEvent<VideosModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()
    val eventTapDiscoverFilter = CommonLiveEvent<FilterParamsModel>()

    //endregion

    //region Actions
    fun doLoadView() {
        model = DiscoverModel.skeleton(model.state)
        eventLoadView.postValue(model)
        model.filters = params.lstFilters
        loadFilters()
        val paramsToSend = params.params
        val hasSelectedFilters = paramsToSend.keys.any()
        if (hasSelectedFilters) doLoadFilteredWorkouts(paramsToSend)

        viewModelScope.launch {
            val response = backendManager.videos()
            if (response.isSuccess) {
                val responseModel = VideosResponseModel.parse(response.responseString)
                if (!hasSelectedFilters) model.workouts = responseModel.workouts
                model.collections = responseModel.collections
                model.programs = responseModel.programs
                if (model.filters.isEmpty()) {
                    model.filters = responseModel.filters.filter {
                        it.identifier.isNotEmpty() && it.values.isNotEmpty()
                    }
                }
            } else {
                model = DiscoverModel(model.state)
            }
            params.lstFilters = model.filters
            eventLoadView.postValue(model)
            loadFilters()
        }
    }

    private fun doLoadFilteredWorkouts(paramsToSend: HashMap<String, Any>) {
        viewModelScope.launch {
            val response = backendManager.videos(paramsToSend)
            model.workouts = if (response.isSuccess) {
                VideosResponseModel.parse(response.responseString).results
            } else emptyList()
            if (model.state == DiscoverViewState.WORKOUTS) eventLoadView.postValue(model)
        }
    }

    fun doTapWorkouts() {
        model.state = DiscoverViewState.WORKOUTS
        eventLoadView.postValue(model)
        loadFilters()
    }

    fun doTapCollections() {
        model.state = DiscoverViewState.COLLECTIONS
        eventLoadView.postValue(model)
        loadFilters()
    }

    fun doTapPrograms() {
        model.state = DiscoverViewState.PROGRAMS
        eventLoadView.postValue(model)
        loadFilters()
    }

    private fun loadFilters() {
        val filters = if (model.state == DiscoverViewState.WORKOUTS) {
            model.filters
        } else {
            val resetFilters = params.copyModel().lstFilters
            resetFilters.onEach { it.values.forEach { model -> model.isSelected = false } }
        }
        val model = DiscoverModel(state = model.state, filters = filters)
        eventLoadFilters.postValue(model)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapFilter() {
        eventTapFilter.postValue(params.copyModel())
    }

    override fun doTapVideo(model: VideoModel) {
        analyticsManager.trackViewVideoDetail(model, DISCOVER)
        eventTapVideo.postValue(model)
    }

    override fun doTapVideos(model: VideosModel) {
        model.state = this.model.state
        eventTapVideos.postValue(model)
    }

    override fun doTapDiscoverFilter(filter: FilterModel) {
        params.selectedModel = filter
        eventTapDiscoverFilter.postValue(params.copyModel())
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(DISCOVER)
    }
    //endregion
}
