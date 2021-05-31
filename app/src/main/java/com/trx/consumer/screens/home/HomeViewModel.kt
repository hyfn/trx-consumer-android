package com.trx.consumer.screens.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.models.common.BannerModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.responses.BannerResponseModel
import com.trx.consumer.models.responses.PromosResponseModel
import com.trx.consumer.models.responses.VideosResponseModel
import com.trx.consumer.screens.promotion.PromoViewListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), PromoViewListener, VideoWorkoutListener {

    //region variables

    private var bannerUrlString: String = ""
    private var promoList: List<PromoModel> = listOf()

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadUser = CommonLiveEvent<UserModel>()
    val eventLoadBanner = CommonLiveEvent<BannerModel>()
    val eventLoadPromos = CommonLiveEvent<List<PromoModel>>()
    val eventLoadVideos = CommonLiveEvent<List<VideoModel>>()

    val eventShowEditProfile = CommonLiveEvent<Void>()
    val eventShowVideo = CommonLiveEvent<VideoModel>()
    val eventShowPromo = CommonLiveEvent<PromoModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    val eventTapBanner = CommonLiveEvent<String>()

    //endregion

    //region Functions

    fun doLoadView() {
        analyticsManager.trackAmplitude(
            AnalyticsEventModel.PAGE_VIEW,
            this.javaClass.simpleName.replace("ViewModel", "")
        )
        eventLoadView.call()
        doLoadUser()
    }

    private fun doLoadUser() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            backendManager.user()
            eventShowHud.postValue(false)
            cacheManager.user()?.let { user ->
                eventLoadUser.postValue(user)
            }
        }
    }

    fun doLoadBanner() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.banner()
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                val model = BannerResponseModel.parse(response.responseString)
                model.banner.let { banner ->
                    eventLoadBanner.postValue(banner)
                    bannerUrlString = banner.offerButtonLink
                }
            }
        }
    }

    fun doLoadPromos() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.promos()
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                val model = PromosResponseModel.parse(response.responseString)
                promoList = model.promos
                eventLoadPromos.postValue(promoList)
            }
        }
    }

    fun doLoadVideos() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.videos()
            if (response.isSuccess) {
                val model = VideosResponseModel.parse(response.responseString)
                eventLoadVideos.postValue(model.workouts)
            }
        }
    }

    fun doShowEditProfile() {
        eventShowEditProfile.call()
    }

    fun doTapBanner() {
        eventTapBanner.postValue(bannerUrlString)
    }

    override fun doTapPromo(model: PromoModel) {
        eventShowPromo.postValue(model)
    }

    override fun doTapVideo(model: VideoModel) {
        eventShowVideo.postValue(model)
        analyticsManager.trackAmplitude(
            AnalyticsEventModel.VIEW_VIDEO_DETAIL,
            model
        )
    }

    //endregion
}
