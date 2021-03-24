package com.trx.consumer.views.input

import java.util.Date

interface InputViewListener {

    fun doStartInput() {}

    fun doUpdateText(userInput: String, isValidInput: Boolean, identifier: InputViewState)

    fun didUpdateText(
        userInput: String,
        isValidInput: Boolean,
        validationError: String?,
        identifier: InputViewState
    ) {
    }

    fun doUpdateDate(date: Date, identifier: InputViewState) {}
}
