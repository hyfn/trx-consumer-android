package com.trx.consumer.screens.loading

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.screens.loading.LoadingViewState

class LoadingViewModel @ViewModelInject constructor(private val backendManager: BackendManager) :
    BaseViewModel() {

    //region Objects
    var state: LoadingViewState? = null

    //endregion

    //region Events
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventLoadView = CommonLiveEvent<LoadingViewState>()

    //endregion

    //region Functions
    fun doLoadView(state: LoadingViewState) {
        eventShowHud.postValue(true)
        this.state = state
        eventLoadView.postValue(state)
    }

    //endregion
}
