package com.trx.consumer.screens.testutility

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.LiveWorkoutModel
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutListener
import com.trx.consumer.screens.promotion.PromotionListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutListener

class TestUtilityViewModel :
    BaseViewModel(),
    LiveWorkoutListener,
    VirtualWorkoutListener,
    VideoWorkoutListener,
    PromotionListener {

    //region Events

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapPlans = CommonLiveEvent<Void>()
    val eventLoadLiveWorkouts = CommonLiveEvent<List<LiveWorkoutModel>>()
    val eventLoadVirtualWorkouts = CommonLiveEvent<List<VirtualWorkoutModel>>()
    val eventLoadVideoWorkouts = CommonLiveEvent<List<VideoModel>>()
    val eventLoadPromotions = CommonLiveEvent<List<PromotionModel>>()

    //endregion

    //region Functions

    fun doLoadView() {
        doLoadLiveWorkouts()
        doLoadVirtualWorkouts()
        doLoadVideoWorkouts()
        doLoadPromotions()
    }

    private fun doLoadLiveWorkouts() {
        eventLoadLiveWorkouts.postValue(LiveWorkoutModel.testList(5))
    }

    private fun doLoadVirtualWorkouts() {
        eventLoadVirtualWorkouts.postValue(VirtualWorkoutModel.testListVariety())
    }

    private fun doLoadVideoWorkouts() {
        eventLoadVideoWorkouts.postValue(VideoModel.testList(5))
    }

    private fun doLoadPromotions() {
        eventLoadPromotions.postValue(PromotionModel.testList(5))
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapPlans() {
        eventTapPlans.call()
    }

    override fun doTapBook(model: LiveWorkoutModel) {}

    override fun doTapSelect(model: LiveWorkoutModel) {}

    override fun doTapPrimary(model: VirtualWorkoutModel) {}

    override fun doTapSelect(model: VirtualWorkoutModel) {}

    override fun doTapSelect(model: VideoModel) {}

    override fun doTapPromotion(model: PromotionModel) {}

    //endregion
}
