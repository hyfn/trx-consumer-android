package com.trx.consumer.screens.update

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.format
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.models.UserModel
import com.trx.consumer.models.responses.UserResponseModel
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TimeZone

class UpdateViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), InputViewListener {

    //region Objects

    var state = UpdateViewState.CREATE

    //endregion

    //region Variables

    private var firstName: String = ""
    private var lastName: String = ""
    private var birthday: String = ""
    private var zipCode: String = ""
    private var checked = false

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

    val eventLoadState = CommonLiveEvent<UpdateViewState>()
    val eventLoadUser = CommonLiveEvent<UserModel>()
    val eventLoadButton = CommonLiveEvent<Boolean>()
    val eventLoadSuccess = CommonLiveEvent<String>()
    val eventLoadError = CommonLiveEvent<String>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapTermsAndConditions = CommonLiveEvent<Void>()
    val eventTapWaivers = CommonLiveEvent<Void>()

    val eventUpdateDate = CommonLiveEvent<String>()

    val eventShowOnboarding = CommonLiveEvent<Void>()
    val eventShowVerification = CommonLiveEvent<Void>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        viewModelScope.launch {
            eventLoadState.postValue(state)
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
            } else eventLoadError.postValue("There was an error")
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapContinue() {
        doCallContinue()
    }

    fun doTapCheckbox(isChecked: Boolean) {
        checked = isChecked
        validate()
    }

    fun doTapTermsAndConditions() {
        eventTapTermsAndConditions.call()
    }

    fun doTapWaivers() {
        eventTapWaivers.call()
    }

    override fun doUpdateDate(date: Date, identifier: InputViewState) {
        val birthday = date.format(format = identifier.dateFormat, zone = TimeZone.getDefault())
        eventUpdateDate.postValue(birthday)
    }

    private fun doCallContinue() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.update(params)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                when (state) {
                    UpdateViewState.CREATE -> {
                        eventShowVerification.call()
                        eventShowOnboarding.call()
                    }
                    UpdateViewState.EDIT -> eventLoadSuccess.postValue("Success: TEMP")
                }
            } else {
                eventLoadError.postValue("Error: TEMP")
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
        val enabled = firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            birthday.isNotEmpty() &&
            zipCode.isNotEmpty() &&
            (if (state == UpdateViewState.CREATE) checked else true)
        eventLoadButton.postValue(enabled)
    }

    //endregion
}
