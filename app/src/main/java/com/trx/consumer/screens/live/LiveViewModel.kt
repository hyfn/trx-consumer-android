package com.trx.consumer.screens.live

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.LiveWorkoutModel
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.promotion.PromotionListener
import com.trx.consumer.screens.trainerprofile.TrainerProfileListener

class LiveViewModel :
    BaseViewModel(),
    LiveWorkoutListener,
    TrainerProfileListener,
    PromotionListener {

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()

    val eventLoadPromotions = CommonLiveEvent<List<PromotionModel>>()
    val eventLoadTrainers = CommonLiveEvent<List<TrainerModel>>()
    val eventLoadWorkoutsToday = CommonLiveEvent<List<LiveWorkoutModel>>()
    val eventLoadWorkoutsTomorrow = CommonLiveEvent<List<LiveWorkoutModel>>()
    val eventLoadWorkoutsRecommended = CommonLiveEvent<List<LiveWorkoutModel>>()
    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doLoadPromotions() {
        eventLoadPromotions.postValue(PromotionModel.testList(5))
    }

    fun doLoadSessions() {
        val liveWorkouts = LiveWorkoutModel.testList(5)
        eventLoadWorkoutsToday.postValue(liveWorkouts)
        eventLoadWorkoutsTomorrow.postValue(liveWorkouts)
        eventLoadWorkoutsRecommended.postValue(liveWorkouts)
    }

    fun doLoadTrainers() {
        eventLoadTrainers.postValue(TrainerModel.testList(5))
    }

    override fun doTapBook(model: LiveWorkoutModel) {}

    override fun doTapSelect(model: LiveWorkoutModel) {}

    override fun doTapTrainerProfile(model: TrainerModel) {}

    override fun doTapPromotion(model: PromotionModel) {}
    //endregion
}
