package com.trx.consumer.screens.home

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.UserModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.screens.promotion.PromotionListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener

class HomeViewModel :
    BaseViewModel(),
    PromotionListener,
    VideoWorkoutListener {

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadUser = CommonLiveEvent<UserModel>()
    val eventLoadBanner = CommonLiveEvent<Boolean>()
    val eventLoadPromos = CommonLiveEvent<List<PromoModel>>()
    val eventLoadVideos = CommonLiveEvent<List<VideoModel>>()

    val eventTapTest = CommonLiveEvent<Void>()
    val eventTapBanner = CommonLiveEvent<Void>()

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
        eventLoadBanner.postValue(true)
    }

    fun doLoadPromos() {
        eventLoadPromos.postValue(PromoModel.testList(5))
    }

    fun doLoadVideos() {
        eventLoadVideos.postValue(VideoModel.testList(5))
    }

    fun doTapBanner() {
        eventTapBanner.call()
    }

    override fun doTapPromo(model: PromoModel) {}
    override fun doTapSelect(model: VideoModel) {}

    //endregion
}
