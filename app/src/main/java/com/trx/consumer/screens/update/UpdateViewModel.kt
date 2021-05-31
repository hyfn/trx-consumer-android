package com.trx.consumer.screens.update

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.format
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.UserResponseModel
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TimeZone

class UpdateViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), InputViewListener {

    //region Variables

    private var firstName: String = ""
    private var lastName: String = ""
    private var birthday: String = ""
    private var zipCode: String = ""

    //endregion

    //region Params

    private val params: HashMap<String, Any>
        get() = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "birthday" to birthday,
            "postalCode" to zipCode
        )

    //endregion

    //region Events

    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadUser = CommonLiveEvent<UserModel>()
    val eventLoadButton = CommonLiveEvent<Boolean>()
    val eventLoadSuccess = CommonLiveEvent<Void>()
    val eventLoadError = CommonLiveEvent<String>()

    val eventTapBack = CommonLiveEvent<Void>()

    val eventUpdateDate = CommonLiveEvent<String>()

    val eventShowVerification = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        viewModelScope.launch {
            analyticsManager.trackAmplitude(
                AnalyticsEventModel.PAGE_VIEW,
                this.javaClass.simpleName.replace("ViewModel", "")
            )
            eventLoadView.call()
            eventShowHud.postValue(true)
            val response = backendManager.user()
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                val model = UserResponseModel.parse(response.responseString)
                model.user.let {
                    firstName = it.firstName
                    lastName = it.lastName
                    birthday = it.birthday
                    zipCode = it.zipCode
                    eventLoadUser.postValue(it)
                }
            } else eventLoadError.postValue(ResponseModel.genericErrorMessage)
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapContinue() {
        doCallUpdate()
    }

    override fun doUpdateDate(date: Date, identifier: InputViewState) {
        val birthday = date.format(format = identifier.dateFormat, zone = TimeZone.getDefault())
        eventUpdateDate.postValue(birthday)
        validate()
    }

    private fun doCallUpdate() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.update(params)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                eventLoadSuccess.call()
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
            InputViewState.FIRST -> firstName = if (isValidInput) userInput else ""
            InputViewState.LAST -> lastName = if (isValidInput) userInput else ""
            InputViewState.BIRTHDAY -> birthday = if (isValidInput) userInput else ""
            InputViewState.ZIPCODE -> zipCode = if (isValidInput) userInput else ""
            else -> {
            }
        }
        validate()
    }

    private fun validate() {
        val enabled = InputViewState.FIRST.validate(firstName) &&
            InputViewState.LAST.validate(lastName) &&
            InputViewState.BIRTHDAY.validate(birthday) &&
            InputViewState.ZIPCODE.validate(zipCode)
        eventLoadButton.postValue(enabled)
    }

    //endregion
}
