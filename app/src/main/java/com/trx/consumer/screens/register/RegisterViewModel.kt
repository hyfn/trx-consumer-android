package com.trx.consumer.screens.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch

class RegisterViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), InputViewListener {

    //region Variables

    var firstName: String = ""
    var lastName: String = ""
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
                "firstName" to firstName,
                "lastName" to lastName,
                "updateTrxUserObject" to hashMapOf(
                    "signUpUserType" to "mobile"
                )
            )
        }

    //endregion

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadButton = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapLogin = CommonLiveEvent<Void>()
    val eventTapTermsConditions = CommonLiveEvent<Void>()

    val eventShowOnboarding = CommonLiveEvent<Void>()
    val eventShowError = CommonLiveEvent<String>()
    val eventValidateError = CommonLiveEvent<Int>()

    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Events

    fun doLoadView() {
        analyticsManager.trackPageView(pageTitle)
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
                if(BuildConfig.kIsVerificationEnabled) {
                    // eventShowVerfication.call()
                } else {
                    analyticsManager.trackSignUp(null, "EMAIL_PW")
                }
                eventShowOnboarding.call()
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
            InputViewState.FIRST -> firstName = if (isValidInput) userInput else ""
            InputViewState.LAST -> lastName = if (isValidInput) userInput else ""
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
        val enabled: Boolean = InputViewState.FIRST.validate(firstName) &&
            InputViewState.LAST.validate(lastName) &&
            InputViewState.EMAIL.validate(email) &&
            InputViewState.PASSWORD.validate(password) &&
            password == confirmPassword &&
            checked
        eventLoadButton.postValue(enabled)
    }

    fun doDismissKeyboard() {
        eventDismissKeyboard.call()
    }

    //endregion
}
