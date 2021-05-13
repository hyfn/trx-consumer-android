package com.trx.consumer.screens.home

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.UserModel
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.promotion.PromotionListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutListener

class HomeViewModel :
    BaseViewModel(),
    PromotionListener,
    VirtualWorkoutListener,
    LiveWorkoutListener,
    VideoWorkoutListener {

    //region Events

    val eventTapTest = CommonLiveEvent<Void>()

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadPromotionsTop = CommonLiveEvent<List<PromotionModel>>()
    val eventLoadPromotionsBottom = CommonLiveEvent<List<PromotionModel>>()
    val eventLoadUpcoming = CommonLiveEvent<List<VirtualWorkoutModel>>()
    val eventLoadBookWith = CommonLiveEvent<List<VirtualWorkoutModel>>()
    val eventLoadLive = CommonLiveEvent<List<WorkoutModel>>()
    val eventLoadOnDemand = CommonLiveEvent<List<VideoModel>>()
    val eventLoadUser = CommonLiveEvent<UserModel>()

    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
        eventLoadUser.postValue(UserModel.test())
    }

    fun doTapTest() {
        eventTapTest.call()
    }

    fun doLoadPromotionsTop() {
        eventLoadPromotionsTop.postValue(PromotionModel.testList(5))
    }

    fun doLoadPromotionsBottom() {
        eventLoadPromotionsBottom.postValue(PromotionModel.testList(5))
    }

    fun doLoadUpcoming() {
        eventLoadUpcoming.postValue(VirtualWorkoutModel.testListVariety())
    }

    fun doLoadBookWith() {
        eventLoadBookWith.postValue(VirtualWorkoutModel.testListVariety())
    }

    fun doLoadLive() {
        eventLoadLive.postValue(WorkoutModel.testList(5))
    }

    fun doLoadOnDemand() {
        eventLoadOnDemand.postValue(VideoModel.testList(5))
    }

    override fun doTapPromotion(model: PromotionModel) {}

    override fun doTapPrimary(model: VirtualWorkoutModel) {}

    override fun doTapSelect(model: VirtualWorkoutModel) {}

    override fun doTapBook(model: WorkoutModel) {}

    override fun doTapSelect(model: WorkoutModel) {}

    override fun doTapSelect(model: VideoModel) {}

    //endregion
}
