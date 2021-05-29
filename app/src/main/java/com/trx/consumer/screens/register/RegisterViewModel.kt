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

    //region Variables

    var email: String = ""
    var password: String = ""
    private var confirmPassword: String = ""
    private var checked = false

    //endregion

    //region Params

    private val params: HashMap<String, Any>
        get() {
            return hashMapOf(
                "email" to email,
                "password" to password,
                "firstName" to "new",
                "lastName" to "user",
                "updateTrxUserObject" to hashMapOf(
                    "signUpUserType" to "mobile"
                )
            )
        }

    //endregion

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadButton = CommonLiveEvent<Boolean>()
    val eventLoadProfile = CommonLiveEvent<Void>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapLogin = CommonLiveEvent<Void>()
    val eventTapTermsConditions = CommonLiveEvent<Void>()

    val eventShowError = CommonLiveEvent<String>()
    val eventValidateError = CommonLiveEvent<Int>()

    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Events

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapCheckbox(isChecked: Boolean) {
        checked = isChecked
        validateButton()
    }

    fun doTapLogin() {
        LogManager.log("doTapLogin")
        eventTapLogin.call()
    }

    fun doTapTermsConditions() {
        eventTapTermsConditions.call()
    }

    fun doCreateAccount() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.register(params)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                eventLoadProfile.call()
            } else {
                eventShowError.postValue(response.errorMessage)
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
            else -> { }
        }
        validateButton()
    }

    private fun validateButton() {
        val enabled: Boolean = InputViewState.EMAIL.validate(email) &&
            InputViewState.PASSWORD.validate(password) &&
            checked
        eventLoadButton.postValue(enabled)
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
        if (password != confirmPassword) {
            val message = InputViewState.CONFIRM_PASSWORD.errorMessage
            eventValidateError.postValue(message)
            return false
        }
        if (!checked) {
            val message = R.string.register_terms_conditions_error
            eventValidateError.postValue(message)
            return false
        }
        return true
    }

    //endregion
}
