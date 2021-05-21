package com.trx.consumer.screens.discover

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.screens.discover.discoverfilter.DiscoverFilterListener
import com.trx.consumer.screens.discover.list.DiscoverListener

class DiscoverViewModel : BaseViewModel(), DiscoverListener, DiscoverFilterListener {

    var params: FilterParamsModel = FilterParamsModel()
    var filters: List<FilterModel> = FilterModel.testList(7)

    val eventLoadWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadCollections = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<WorkoutModel>>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapDiscover = CommonLiveEvent<WorkoutModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()
    val eventTapDiscoverFilter = CommonLiveEvent<FilterParamsModel>()
    val eventLoadFilters = CommonLiveEvent<List<FilterModel>>()

    fun doLoadView() {
        params.list = filters
        eventLoadFilters.postValue(filters)
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
