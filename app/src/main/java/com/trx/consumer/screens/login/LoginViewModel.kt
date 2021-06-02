package com.trx.consumer.screens.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), InputViewListener {

    //region Variables

    var email: String = ""
    var password: String = ""

    //endregion

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadButton = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapForgotPassword = CommonLiveEvent<Void>()
    val eventTapLogin = CommonLiveEvent<Void>()
    val eventTapSignUp = CommonLiveEvent<Void>()

    val eventShowError = CommonLiveEvent<String>()
    val eventValidateError = CommonLiveEvent<Int>()

    val eventShowOnboarding = CommonLiveEvent<Void>()
    val eventDismissKeyboard = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        analyticsManager.trackAmplitude(AnalyticsEventModel.PAGE_VIEW, pageTitle)
        eventLoadView.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapForgotPassword() {
        eventTapForgotPassword.call()
    }

    fun doTapLogin() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.login(email, password)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                analyticsManager.trackAmplitude(AnalyticsEventModel.SIGN_IN)
                if (cacheManager.didShowOnboarding()) {
                    eventTapLogin.call()
                } else {
                    eventShowOnboarding.call()
                }
            } else {
                eventShowError.postValue(response.errorMessage)
            }
        }
    }

    fun doTapSignUp() {
        eventTapSignUp.call()
    }

    override fun doUpdateText(
        userInput: String,
        isValidInput: Boolean,
        identifier: InputViewState
    ) {
        when (identifier) {
            InputViewState.EMAIL -> email = if (isValidInput) userInput else ""
            InputViewState.PASSWORD -> password = if (isValidInput) userInput else ""
            else -> { }
        }
        validate()
    }

    private fun validate() {
        val enabled: Boolean = InputViewState.EMAIL.validate(email) &&
            InputViewState.PASSWORD.validate(password)
        eventLoadButton.postValue(enabled)
    }

    fun doDismissKeyboard() {
        eventDismissKeyboard.call()
    }

    //endregion
}
