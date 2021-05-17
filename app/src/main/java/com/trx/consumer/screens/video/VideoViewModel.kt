package com.trx.consumer.screens.video

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.video.list.VideoListener

class VideoViewModel : BaseViewModel(), VideoListener {

    val eventLoadWorkouts = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadCollections = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadPrograms = CommonLiveEvent<List<WorkoutModel>>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapVideo = CommonLiveEvent<WorkoutModel>()

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

    override fun doTapVideo(model: WorkoutModel) {
        eventTapVideo.postValue(model)
    }
}
