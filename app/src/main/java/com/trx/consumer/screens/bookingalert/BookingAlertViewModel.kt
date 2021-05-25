package com.trx.consumer.screens.bookingalert

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.BookingAlertModel

class BookingAlertViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel() {

    var model: BookingAlertModel? = null
    var isCallingBook = false
    private val isGuest = NavigationManager.shared.isGuestMode

    val messageBookingConfirmed = "Your booking has been successfully confirmed"
    val messageBookingCancelled = "Your booking has been successfully cancelled"

    val eventLoadView = CommonLiveEvent<BookingAlertModel>()
    val eventLoadAddCard = CommonLiveEvent<Void>()

    val eventShowPolicy = CommonLiveEvent<Void>()
    val eventShowSuccess = CommonLiveEvent<String>()
    val eventShowError = CommonLiveEvent<String>()

    var eventTapClose = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    fun doLoadView() {
        eventLoadView.postValue(model)
        // model = bookingAlertParamsModel.classModel
        // isCallingBook = bookingAlertParamsModel.isCallingBook
        // model?.let { classModel ->
        //     if (isCallingBook) {
        //         doCallBook()
        //     } else {
        //         eventLoadView.postValue(classModel)
        //     }
        // } ?: eventTapClose.call()
    }

    fun doTapPrimary() {
        // model?.let { classModel ->
        //     when (classModel.state) {
        //         ClassViewState.BOOK -> {
        //             if (isGuest) eventShowGuest.call() else eventShowConfirm.postValue(classModel)
        //         }
        //         ClassViewState.BOOKED -> eventShowCancel.call()
        //         else -> return
        //     }
        // }
    }

    fun doTapSecondary() {
        eventTapClose.call()
    }

    fun doCallConfirm() {
        if (isCallingBook) doCallBook() else doCallWaivers()
    }

    fun doCallBook() {
        // viewModelScope.launch {
        //     model?.apply {
        //         val request = BookRequestModel(identifier, studio.identifier, points)
        //         eventShowHud.postValue(true)
        //         val response = backendManager.book(request)
        //         eventShowHud.postValue(false)
        //         if (response.isSuccess) {
        //             backendManager.profile()
        //             eventShowSuccess.postValue(messageBookingConfirmed)
        //         } else {
        //             if (response.errorMessage.isNotEmpty()) {
        //                 eventShowError.postValue(response.errorMessage)
        //             } else {
        //                 eventShowError.postValue(ResponseModel.genericErrorMessage)
        //             }
        //         }
        //     } ?: eventShowError.postValue(ResponseModel.genericErrorMessage)
        // }
    }

    fun doCallCancel() {
        // model?.apply {
        //     val request = CancelRequestModel(identifier, studio.identifier)
        //     viewModelScope.launch {
        //         eventShowHud.postValue(true)
        //         val response = backendManager.cancel(request)
        //         eventShowHud.postValue(false)
        //         if (response.isSuccess) {
        //             backendManager.profile()
        //             eventShowSuccess.postValue(messageBookingCancelled)
        //         } else {
        //             if (response.errorMessage.isNotEmpty()) {
        //                 eventShowError.postValue(response.errorMessage)
        //             } else {
        //                 eventShowError.postValue(ResponseModel.genericErrorMessage)
        //             }
        //         }
        //     }
        // }
    }

    private fun doCallWaivers() {
        // viewModelScope.launch {
        //     model?.let { classModel ->
        //         eventShowHud.postValue(true)
        //         val response = backendManager.waivers(classModel.studio.identifier)
        //         eventShowHud.postValue(false)
        //         if (response.isSuccess) {
        //             try {
        //                 val responseModel = WaiversResponseModel.parse(response.responseString)
        //                 responseModel.waivers?.let { waivers ->
        //                     if (waivers.terms.isEmpty()) {
        //                         doCallBook()
        //                     } else {
        //                         isCallingBook = true
        //                         val contentModel = ContentParamsModel().apply {
        //                             model = ContentModel(
        //                                 title = "Terms & Conditions",
        //                                 waiverModel = waivers,
        //                                 state = ContentViewState.BOOK
        //                             )
        //                             this.classModel = classModel
        //                         }
        //                         eventShowWaiver.postValue(contentModel)
        //                     }
        //                 }
        //             } catch (e: Exception) {
        //                 LogManager.log(e)
        //             }
        //         } else {
        //             eventShowError.postValue(ResponseModel.genericErrorMessage)
        //         }
        //     }
        // }
    }
}
