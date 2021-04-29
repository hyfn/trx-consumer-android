package com.trx.consumer.screens.update

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.format
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import java.util.Date
import java.util.TimeZone

class UpdateViewModel : BaseViewModel(), InputViewListener {

    var state = UpdateViewState.CREATE
    var firstName: String = ""
    var lastName: String = ""
    var birthday: String = ""
    var zipCode: String = ""

    val eventLoadView = CommonLiveEvent<UpdateViewState>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventUpdateDate = CommonLiveEvent<String>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(state)
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
    }

    override fun doUpdateDate(date: Date, identifier: InputViewState) {
        val birthday = date.format(format = identifier.dateFormat, zone = TimeZone.getDefault())
        eventUpdateDate.postValue(birthday)
    }
}
