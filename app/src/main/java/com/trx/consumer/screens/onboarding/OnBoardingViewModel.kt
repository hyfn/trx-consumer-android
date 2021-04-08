package com.trx.consumer.screens.onboarding

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class OnBoardingViewModel : BaseViewModel() {

    var state: OnBoardingViewState = OnBoardingViewState.VIRTUAL

    val eventLoadView = CommonLiveEvent<OnBoardingViewState>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapNext = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.postValue(state)
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun onBackPressed() {
        eventTapClose.call()
    }

    fun doTapNext() {
        if (state.currentPage < 2) {
            state = OnBoardingViewState.getStateFromPage(state.currentPage + 1)
            eventLoadView.postValue(state)
        } else {
            eventTapNext.call()
        }
    }

    fun doTapPrevious() {
        if (state.currentPage != 0) {
            state = OnBoardingViewState.getStateFromPage(state.currentPage - 1)
            eventLoadView.postValue(state)
        } else {
            doTapBack()
        }
    }
}
