package com.trx.consumer.screens.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.CacheManager
import kotlinx.coroutines.launch

class OnboardingViewModel @ViewModelInject constructor(
    private val cacheManager: CacheManager
) : BaseViewModel() {

    var state: OnBoardingViewState = OnBoardingViewState.DEMAND

    val eventLoadView = CommonLiveEvent<OnBoardingViewState>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventShowRestore = CommonLiveEvent<Void>()

    fun doLoadView() {
        viewModelScope.launch {
            cacheManager.didShowOnboarding(true)
            eventLoadView.postValue(state)
        }
    }

    fun doTapClose() {
        viewModelScope.launch {
            if (cacheManager.didShowRestore()) {
                eventTapClose.call()
            } else {
                eventShowRestore.call()
            }
        }
    }

    fun onBackPressed() {
        doTapPrevious()
    }

    fun doTapNext() {
        if (state.currentPage < 2) {
            state = OnBoardingViewState.getStateFromPage(state.currentPage + 1)
            eventLoadView.postValue(state)
        } else {
            doTapClose()
        }
    }

    private fun doTapPrevious() {
        if (state.currentPage != 0) {
            state = OnBoardingViewState.getStateFromPage(state.currentPage - 1)
            eventLoadView.postValue(state)
        } else {
            doTapClose()
        }
    }
}
