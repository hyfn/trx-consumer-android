package com.trx.consumer.screens.videos

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.discover.list.DiscoverListener

class VideosViewModel : BaseViewModel(), DiscoverListener {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<WorkoutModel>()

    lateinit var workoutModel: WorkoutModel

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(workoutModel)
    }

    override fun doTapDiscover(model: WorkoutModel) {
    }
}
