package com.trx.consumer.screens.update

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.format
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TimeZone

class UpdateViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager
) : BaseViewModel(), InputViewListener {

    var state = UpdateViewState.CREATE
    private var firstName: String = ""
    private var lastName: String = ""
    private var birthday: String = ""
    private var zipCode: String = ""
    private var checked = false

    val eventLoadView = CommonLiveEvent<UpdateViewState>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventUpdateDate = CommonLiveEvent<String>()
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventLoadButton = CommonLiveEvent<Boolean>()

    val eventLoadSuccess = CommonLiveEvent<String>()
    val eventLoadError = CommonLiveEvent<String>()

    val eventShowOnboarding = CommonLiveEvent<Void>()
    val eventShowVerification = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapSave() {
        doCallSave()
    }

    fun doTapCheckbox(isChecked: Boolean) {
        checked = isChecked
        validate()
    }

    fun doLoadView() {
        eventLoadView.postValue(state)
    }

    private fun doCallSave() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.save(requestSave)
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
            (if (state == UpdateViewState.CREATE) true else checked)
        eventLoadButton.postValue(enabled)
    }

    override fun doUpdateDate(date: Date, identifier: InputViewState) {
        val birthday = date.format(format = identifier.dateFormat, zone = TimeZone.getDefault())
        eventUpdateDate.postValue(birthday)
    }

    private val requestSave: HashMap<String, Any>
        get() = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "birthday" to birthday,
            "postalCode" to zipCode
        )
}
