package com.trx.consumer.screens.live

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
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
    val eventLoadWorkoutsToday = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadWorkoutsTomorrow = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadWorkoutsRecommended = CommonLiveEvent<List<WorkoutModel>>()
    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doLoadPromotions() {
        eventLoadPromotions.postValue(PromotionModel.testList(5))
    }

    fun doLoadSessions() {
        val liveWorkouts = WorkoutModel.testList(5)
        eventLoadWorkoutsToday.postValue(liveWorkouts)
        eventLoadWorkoutsTomorrow.postValue(liveWorkouts)
        eventLoadWorkoutsRecommended.postValue(liveWorkouts)
    }

    fun doLoadTrainers() {
        eventLoadTrainers.postValue(TrainerModel.testList(5))
    }

    override fun doTapBook(model: WorkoutModel) {}

    override fun doTapSelect(model: WorkoutModel) {}

    override fun doTapTrainerProfile(model: TrainerModel) {}

    override fun doTapPromotion(model: PromotionModel) {}
    //endregion
}
