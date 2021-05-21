package com.trx.consumer.screens.discover

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.models.responses.VideoResponseModel
import com.trx.consumer.screens.discover.discoverfilter.DiscoverFilterListener
import com.trx.consumer.screens.discover.list.DiscoverListener
import kotlinx.coroutines.launch

class DiscoverViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), DiscoverListener, DiscoverFilterListener {

    var params: FilterParamsModel = FilterParamsModel()
    var filters: List<FilterModel> = FilterModel.testList(7)

    val eventLoadWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadCollections = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadFilters = CommonLiveEvent<List<FilterModel>>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapDiscover = CommonLiveEvent<WorkoutModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()
    val eventTapDiscoverFilter = CommonLiveEvent<FilterParamsModel>()

    fun doLoadView() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.videos()
            if(response.isSuccess){
                val model = VideoResponseModel.parse(response.responseString)
                filters = model.filters
            }
            eventShowHud.postValue(false)
            params.lstFilters = filters
            eventLoadFilters.postValue(filters)
        }
        doLoadWorkouts()
    }

    fun doLoadWorkouts() {
        eventLoadWorkouts.postValue(WorkoutModel.testList(15))
    }

    fun doLoadCollections() {
        eventLoadCollections.postValue(WorkoutModel.testList(15))
    }

    fun doLoadPrograms() {
        eventLoadPrograms.postValue(WorkoutModel.testList(15))
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapFilter() {
        eventTapFilter.postValue(params)
    }

    override fun doTapDiscover(model: WorkoutModel) {
        eventTapDiscover.postValue(model)
    }

    override fun doTapDiscoverFilter(filter: FilterModel) {
        params.selectedModel = filter
        eventTapDiscoverFilter.postValue(params)
    }
}
