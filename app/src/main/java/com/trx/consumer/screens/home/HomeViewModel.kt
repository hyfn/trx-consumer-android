package com.trx.consumer.screens.home

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.UserModel
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.screens.promotion.PromotionListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener

class HomeViewModel :
    BaseViewModel(),
    PromotionListener,
    VideoWorkoutListener {

    //region Events

    val eventTapTest = CommonLiveEvent<Void>()

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadPromotionsBottom = CommonLiveEvent<List<PromotionModel>>()
    val eventLoadOnDemand = CommonLiveEvent<List<VideoModel>>()
    val eventLoadUser = CommonLiveEvent<UserModel>()
    val eventTapPrimary = CommonLiveEvent<Void>()

    val eventLoadBannerView = CommonLiveEvent<Boolean>()

    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
        eventLoadUser.postValue(UserModel.test())
    }

    fun doTapTest() {
        eventTapTest.call()
    }

    fun doLoadBanner() {
        eventLoadBannerView.postValue(true)
    }

    fun doLoadPromotionsBottom() {
        eventLoadPromotionsBottom.postValue(PromotionModel.testList(5))
    }

    fun doLoadOnDemand() {
        eventLoadOnDemand.postValue(VideoModel.testList(5))
    }

    fun doTapPrimary() {
        eventTapPrimary.call()
    }

    override fun doTapPromotion(model: PromotionModel) {}
    override fun doTapSelect(model: VideoModel) {}

    //endregion
}
