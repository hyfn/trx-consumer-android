package com.trx.consumer.screens.discover

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.models.responses.VideoResponseModel
import com.trx.consumer.screens.discover.discoverfilter.DiscoverFilterListener
import com.trx.consumer.screens.discover.list.DiscoverListener
import kotlinx.coroutines.launch

class DiscoverViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), DiscoverListener, DiscoverFilterListener {

    var workouts: List<VideoModel> = listOf()
    var collections: List<VideoModel> = listOf()
    var programs: List<VideoModel> = listOf()
    var params: FilterParamsModel = FilterParamsModel()
    var filters: List<FilterModel> = listOf()

    val eventLoadWorkouts = CommonLiveEvent<List<VideoModel>>()
    val eventLoadCollections = CommonLiveEvent<List<VideoModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<VideoModel>>()
    val eventLoadFilters = CommonLiveEvent<List<FilterModel>>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapDiscover = CommonLiveEvent<VideoModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()
    val eventTapDiscoverFilter = CommonLiveEvent<FilterParamsModel>()

    fun doLoadView() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.videos()
            if (response.isSuccess) {
                val model = VideoResponseModel.parse(response.responseString)
                workouts = model.workouts
                collections = model.collections
                programs = model.programs
                filters = model.filters
            }
            eventShowHud.postValue(false)
            params.lstFilters = filters
            eventLoadFilters.postValue(filters)
            doLoadWorkouts()
        }
    }

    fun doLoadWorkouts() {
        eventLoadWorkouts.postValue(workouts)
    }

    fun doLoadCollections() {
        eventLoadCollections.postValue(collections)
    }

    fun doLoadPrograms() {
        eventLoadPrograms.postValue(programs)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapFilter() {
        eventTapFilter.postValue(params)
    }

    override fun doTapDiscover(model: VideoModel) {
        eventTapDiscover.postValue(model)
    }

    override fun doTapDiscoverFilter(filter: FilterModel) {
        params.selectedModel = filter
        eventTapDiscoverFilter.postValue(params)
    }
}
