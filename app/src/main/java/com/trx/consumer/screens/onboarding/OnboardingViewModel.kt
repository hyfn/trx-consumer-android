package com.trx.consumer.screens.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.CacheManager
import kotlinx.coroutines.launch

class OnboardingViewModel @ViewModelInject constructor(
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    var state: OnboardingViewState = OnboardingViewState.VIRTUAL

    val eventLoadView = CommonLiveEvent<OnboardingViewState>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventTapNext = CommonLiveEvent<Void>()

    fun doLoadView() {
        viewModelScope.launch {
            analyticsManager.trackPageView(pageTitle)
            cacheManager.didShowOnboarding(true)
            eventLoadView.postValue(state)
        }
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    fun onBackPressed() {
        eventTapClose.call()
    }

    fun doTapNext() {
        if (state.currentPage < 2) {
            state = OnboardingViewState.getStateFromPage(state.currentPage + 1)
            eventLoadView.postValue(state)
        } else {
            eventTapNext.call()
        }
    }

    fun doTapPrevious() {
        if (state.currentPage != 0) {
            state = OnboardingViewState.getStateFromPage(state.currentPage - 1)
            eventLoadView.postValue(state)
        }
    }
}
