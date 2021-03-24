package com.trx.consumer.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText
import com.trx.consumer.views.input.InputViewHandler
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState

fun TextInputEditText.setInputViewHandler(state: InputViewState, listener: InputViewListener) {
    val handler = InputViewHandler(this, state, listener)
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            val inputString = text?.toString() ?: ""
            handler.textFieldDidEndEditing(inputString)
        }
    }
}

fun TextInputEditText.dismiss() {
    clearFocus()
    val inputMethodManager = context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
}
