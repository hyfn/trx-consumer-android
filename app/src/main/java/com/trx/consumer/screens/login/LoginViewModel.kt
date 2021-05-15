package com.trx.consumer.screens.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), InputViewListener {

    val eventLoadView = CommonLiveEvent<Void>()
    val eventShowError = CommonLiveEvent<String>()
    val eventTapLogin = CommonLiveEvent<Void>()
    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventValidateError = CommonLiveEvent<Int>()
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapSignUp = CommonLiveEvent<Void>()
    val eventTapForgotPassword = CommonLiveEvent<Void>()

    var email: String = ""
    var password: String = ""

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doTapLogin() {
        if (!validate()) return
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.login(email, password)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                eventTapLogin.call()
            } else {
                eventShowError.postValue(response.errorMessage)
            }
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapSignUp() {
        eventTapSignUp.call()
    }

    fun doTapForgotPassword() {
        eventTapForgotPassword.call()
    }

    override fun doUpdateText(
        userInput: String,
        isValidInput: Boolean,
        identifier: InputViewState
    ) {
        when (identifier) {
            InputViewState.EMAIL -> email = if (isValidInput) userInput else ""
            InputViewState.PASSWORD -> password = if (isValidInput) userInput else ""
            else -> {
            }
        }
    }

    fun doDismissKeyboard() {
        eventDismissKeyboard.call()
    }

    private fun validate(): Boolean {
        if (!InputViewState.EMAIL.validate(email)) {
            val message = InputViewState.EMAIL.errorMessage
            eventValidateError.postValue(message)
            return false
        }
        if (!InputViewState.PASSWORD.validate(password)) {
            val message = InputViewState.PASSWORD.errorMessage
            eventValidateError.postValue(message)
            return false
        }
        return true
    }
}
