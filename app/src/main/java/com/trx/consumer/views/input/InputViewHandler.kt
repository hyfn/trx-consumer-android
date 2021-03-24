package com.trx.consumer.views.input

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class InputViewHandler(
    private val editTextView: TextInputEditText,
    private var mState: InputViewState,
    private var mListener: InputViewListener? = null
) : TextWatcher {

    init {
        initInputViewHandler()
    }

    private fun initInputViewHandler() {
        editTextView.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mListener?.doStartInput()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var updateText = s.toString()
        val should = mState.shouldChange(updateText)
        should?.let { isValid ->
            if (isValid) {
                mListener?.doUpdateText(updateText, should, mState)
                return
            } else {
                updateText = updateText.substring(IntRange(0, start - count))
                editTextView.setText(updateText, TextView.BufferType.EDITABLE)
                editTextView.setSelection(updateText.length)
            }
        }
        val format = mState.format(editTextView, before, count)
        format?.let { isValid ->
            if (isValid) {
                mListener?.doUpdateText(updateText, format, mState)
                return
            } else {
                updateText = updateText.substring(IntRange(0, start - count))
                editTextView.setText(updateText, TextView.BufferType.EDITABLE)
                editTextView.setSelection(updateText.length)
            }
        }
        mListener?.doUpdateText(updateText, true, mState)
    }

    override fun afterTextChanged(s: Editable?) {}

    fun updateDate(date: Date) {
        mListener?.doUpdateDate(date, mState)
    }

    fun textFieldDidEndEditing(inputString: String) {
        val isValidInput = mState.validate(inputString)
        val validationError = if (isValidInput) {
            null
        } else {
            editTextView.context.getString(mState.errorMessage)
        }
        mListener?.didUpdateText(inputString, isValidInput, validationError, mState)
    }

    fun setHandlerListener(listener: InputViewListener) {
        mListener = listener
    }

    fun setHandlerState(state: InputViewState) {
        mState = state
    }
}
