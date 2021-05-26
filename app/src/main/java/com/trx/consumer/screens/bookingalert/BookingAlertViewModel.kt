package com.trx.consumer.screens.bookingalert

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.BookingAlertModel
import kotlinx.coroutines.launch

class BookingAlertViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel() {

    //region Objects

    lateinit var model: BookingAlertModel

    //endregion

    //region Initializers

    val eventLoadView = CommonLiveEvent<BookingAlertModel>()
    val eventLoadAddCard = CommonLiveEvent<Void>()

    val eventShowPolicy = CommonLiveEvent<Void>()
    val eventShowSuccess = CommonLiveEvent<String>()
    val eventShowError = CommonLiveEvent<String>()

    var eventTapClose = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        eventLoadView.postValue(model)
    }

    fun doTapAddPayment() {
        eventLoadAddCard.call()
    }

    fun doTapBook() {
        doCallBook()
    }

    fun doTapCancelYes() {
        doCallCancel()
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    fun doTapPolicy() {
        eventShowPolicy.call()
    }

    fun doCallBook() {
        viewModelScope.launch {
            //  TODO: Add function body
            // when (model.workout.workoutState)
        }
    }

    fun doCallCancel() {
        viewModelScope.launch {
            //  TODO: Add function body
            // when (model.workout.workoutState)
        }
    }

    //endregion
}
