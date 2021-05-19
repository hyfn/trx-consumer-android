package com.trx.consumer.screens.discover

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.discover.list.DiscoverListener

class DiscoverViewModel : BaseViewModel(), DiscoverListener {

    val eventLoadWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadCollections = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<WorkoutModel>>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapDiscover = CommonLiveEvent<WorkoutModel>()

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

    override fun doTapDiscover(model: WorkoutModel) {
        eventTapDiscover.postValue(model)
    }
}
