package com.trx.consumer.screens.email

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch

class EmailViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), InputViewListener {

    //region Objects

    var state: EmailViewState = EmailViewState.FORGOT

    //endregion

    //region Variables

    var email: String = ""
    var code: String = ""

    //endregion

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadState = CommonLiveEvent<EmailViewState>()
    val eventLoadButton = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()

    val eventSendEmailSuccess = CommonLiveEvent<Int>()
    val eventSendEmailError = CommonLiveEvent<String>()

    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        eventLoadView.call()
        eventLoadState.postValue(state)
        eventLoadButton.postValue(false)
    }

    private fun doCallForgot() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.forgot(email)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                eventSendEmailSuccess.postValue(state.success)
            } else {
                eventSendEmailError.postValue(response.errorMessage)
            }
        }
    }

    //  TODO: Add function body when CODE endpoint is implemented.
    private fun doCallCode() {
        viewModelScope.launch {
            LogManager.log("doCallCode")
            // eventShowHud.postValue(true)
            // val response = backendManager.code(code)
            // eventShowHud.postValue(false)
            // if (response.isSuccess) {
            //     eventSendEmailSuccess.postValue(state.success)
            // } else {
            //     eventSendEmailError.postValue(state.error)
            // }
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapSendEmail() {
        when (state) {
            EmailViewState.FORGOT -> doCallForgot()
            EmailViewState.CODE -> doCallCode()
        }
    }

    override fun doUpdateText(
        userInput: String,
        isValidInput: Boolean,
        identifier: InputViewState
    ) {
        when (identifier) {
            InputViewState.EMAIL -> email = if (isValidInput) userInput.trim() else ""
            InputViewState.CODE -> code = if (isValidInput) userInput else ""
            else -> { }
        }
        validate()
    }

    private fun validate() {
        val enabled: Boolean = when (state) {
            EmailViewState.FORGOT -> InputViewState.EMAIL.validate(email)
            EmailViewState.CODE -> InputViewState.CODE.validate(code)
        }
        eventLoadButton.postValue(enabled)
    }

    fun doDismissKeyboard() {
        eventDismissKeyboard.call()
    }

    //endregion
}
