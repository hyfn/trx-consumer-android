package com.trx.consumer.screens.testutility

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.LiveWorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener

class TestUtilityViewModel : BaseViewModel(), LiveWorkoutListener {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadLiveWorkouts = CommonLiveEvent<List<LiveWorkoutModel>>()


    //endregion

    //region Functions

    fun doLoadView() {
        doLoadLiveWorkouts()
    }

    private fun doLoadLiveWorkouts() {
        eventLoadLiveWorkouts.postValue(LiveWorkoutModel.testList(5))
    }

    override fun doTapBook(model: LiveWorkoutModel) {}

    override fun doTapSelect(model: LiveWorkoutModel) {}

    fun doTapBack() {
        eventTapBack.call()
    }

    //endregion
}
