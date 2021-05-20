package com.trx.consumer.screens.cards

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.CardModel
import kotlinx.coroutines.launch

class CardsViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel() {

    //region Objects

    var model: CardModel? = null

    //endregion

    //region Events

    val eventLoadViewSuccess = CommonLiveEvent<CardModel?>()
    val eventLoadViewFailure = CommonLiveEvent<Void>()
    val eventTapAdd = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapReplace = CommonLiveEvent<Void>()

    val eventLoadView = CommonLiveEvent<CardModel?>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.user()
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                cacheManager.user()?.card?.let {
                    model = it
                    eventLoadViewSuccess.postValue(model)
                }
            } else {
                eventLoadViewFailure.call()
            }
        }
    }

    fun doTapAdd() {
        eventTapAdd.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun onBackPressed() {
        eventTapBack.call()
    }

    fun doTapReplace() {
        eventTapReplace.call()
    }

    //endregion
}
