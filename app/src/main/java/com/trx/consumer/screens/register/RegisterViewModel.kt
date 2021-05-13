package com.trx.consumer.screens.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch

class RegisterViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), InputViewListener {

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventTapLogin = CommonLiveEvent<Void>()
    val eventTapTermsConditions = CommonLiveEvent<Void>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadButton = CommonLiveEvent<Boolean>()
    val eventLoadProfile = CommonLiveEvent<Void>()
    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventValidateError = CommonLiveEvent<Int>()
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventTapBack = CommonLiveEvent<Void>()

    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var checked = false

    //endregion

    //region Functions

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doTapLogin() {
        LogManager.log("doTapLogin")
        eventTapLogin.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapCheckbox(isChecked: Boolean) {
        checked = isChecked
    }

    fun doTapTermsConditions() {
        eventTapTermsConditions.call()
    }

    fun doCreateAccount() {
        if (!InputViewState.EMAIL.validate(email)) {
            val message = InputViewState.EMAIL.errorMessage
            eventValidateError.postValue(message)
            return
        }
        if (!InputViewState.PASSWORD.validate(password)) {
            val message = InputViewState.PASSWORD.errorMessage
            eventValidateError.postValue(message)
            return
        }
        if (password != confirmPassword) {
            val message = InputViewState.CONFIRM_PASSWORD.errorMessage
            eventValidateError.postValue(message)
            return
        }
        if (!checked) {
            val message = R.string.register_terms_conditions_error
            eventValidateError.postValue(message)
            return
        }
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.register(params)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                eventLoadProfile.call()
            } else {
                eventLoadError.postValue(response.errorMessage)
            }
        }
    }

    override fun doUpdateText(
        userInput: String,
        isValidInput: Boolean,
        identifier: InputViewState
    ) {
        when (identifier) {
            InputViewState.EMAIL -> email = if (isValidInput) userInput else ""
            InputViewState.PASSWORD -> password = if (isValidInput) userInput else ""
            InputViewState.CONFIRM_PASSWORD -> {
                confirmPassword = if (userInput == password) userInput else ""
            }
            else -> {
            }
        }
    }

    fun doDismissKeyboard() {
        eventDismissKeyboard.call()
    }

    //endregion

    private val params: Map<String, Any>
        get() {
            return mapOf(
                "email" to email,
                "password" to password,
                "firstName" to "new",
                "lastName" to "user"
            )
        }
}
