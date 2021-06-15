package com.trx.consumer.screens.loading

import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val backendManager: BackendManager
) : BaseViewModel() {

    //region Objects
    lateinit var state: LoadingViewState

    //endregion

    //region Events
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventLoadView = CommonLiveEvent<LoadingViewState>()

    val eventLoadAuth = CommonLiveEvent<Void>()
    val eventLoadAuthSuccess = CommonLiveEvent<Void>()
    val eventLoadAuthFailure = CommonLiveEvent<String>()

    //endregion

    //region Functions
    fun doLoadView(state: LoadingViewState) {
        eventShowHud.postValue(true)
        this.state = state
        eventLoadView.postValue(state)
        when (state) {
            LoadingViewState.LAUNCH -> eventLoadAuth.call()
        }
    }

    fun doLoadAuth() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.auth()
            if (response.isSuccess) {
                eventLoadAuthSuccess.call()
            } else {
                backendManager.updateBeforeLogout()
                eventLoadAuthFailure.postValue(response.errorMessage)
            }
            eventShowHud.postValue(false)
        }
    }

    //endregion
}
