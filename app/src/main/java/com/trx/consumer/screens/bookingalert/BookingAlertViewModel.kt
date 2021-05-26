package com.trx.consumer.screens.bookingalert

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.responses.BookIntentResponseModel
import com.trx.consumer.screens.workout.WorkoutViewState
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

    private fun doCallBook() {
        viewModelScope.launch {
            when (model.workout.workoutState) {
                WorkoutViewState.LIVE -> {
                    eventShowHud.postValue(true)
                    val intentResponse = backendManager.bookSessionIntent(
                        model.workout.paramsSessionIntent
                    )
                    eventShowHud.postValue(false)

                    if (intentResponse.isSuccess) {
                        try {
                            val responseModel = BookIntentResponseModel.parse(
                                intentResponse.responseString
                            )
                            val params = model.workout.paramsSessionConfirm(responseModel.invoiceId)
                            val confirmResponse = backendManager.bookSessionConfirm(params)
                            if (confirmResponse.isSuccess) {
                                eventShowSuccess.postValue("YOUR SESSION IS BOOKED!")
                                //  TODO: Implement this in cacheManager.
                                // cacheManager.asyncHomeRefresh()
                            } else {
                                eventShowError.postValue(
                                    "THERE WAS A SESSION CONFIRM ERROR"
                                )
                            }
                        } catch (e: Exception) {
                            LogManager.log(e)
                            e.message?.let { eventShowError.postValue(it) }
                        }
                    } else {
                        eventShowError.postValue("THERE WAS A SESSION INTENT ERROR")
                    }
                }
                WorkoutViewState.VIRTUAL -> {
                    eventShowHud.postValue(true)
                    val intentResponse = backendManager.bookProgramIntent(
                        model.workout.paramsProgramIntent
                    )
                    eventShowHud.postValue(false)

                    if (intentResponse.isSuccess) {
                        try {
                            val responseModel = BookIntentResponseModel.parse(
                                intentResponse.responseString
                            )
                            val params = model.workout.paramsProgramConfirm(responseModel.invoiceId)
                            val confirmResponse = backendManager.bookProgramConfirm(params)
                            if (confirmResponse.isSuccess) {
                                eventShowSuccess.postValue("YOUR SESSION IS BOOKED!")
                                //  TODO: Implement this in cacheManager.
                                // cacheManager.asyncVirtualRefresh()
                            } else {
                                eventShowError.postValue(
                                    "THERE WAS A SESSION CONFIRM ERROR"
                                )
                            }
                        } catch (e: Exception) {
                            LogManager.log(e)
                            e.message?.let { eventShowError.postValue(it) }
                        }
                    } else {
                        eventShowError.postValue("THERE WAS A SESSION INTENT ERROR")
                    }
                }
                else -> LogManager.log("doCallBook")
            }
        }
    }

    private fun doCallCancel() {
        viewModelScope.launch {
            model.workout.cancelId?.let { id ->
                eventShowHud.postValue(true)
                val response = backendManager.bookCancel(id)
                eventShowHud.postValue(false)

                if (response.isSuccess) {
                    eventShowSuccess.postValue("SESSION CANCELLED!")
                    //  TODO: Implement asyncHomeFresh and asyncVirtualRefresh in cacheManager.
                    // cacheManager.asyncHomeRefresh()
                    // cacheManager.asyncVirtualRefresh()
                } else {
                    eventShowError.postValue("THERE WAS A CANCEL ERROR")
                }
            }
        }
    }

    //endregion
}
