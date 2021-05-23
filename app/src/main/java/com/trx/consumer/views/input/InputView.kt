package com.trx.consumer.views.input

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.trx.consumer.R
import com.trx.consumer.common.CommonView
import com.trx.consumer.databinding.LayoutInputBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.date
import com.trx.consumer.extensions.dismiss
import com.trx.consumer.extensions.getInputType
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.screens.datepicker.DatePickerModel
import com.trx.consumer.screens.email.EmailViewState
import java.util.TimeZone

class InputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CommonView(context, attrs) {

    private val binding =
        LayoutInputBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var mState: InputViewState
    private var isShowingLabel: Boolean = false
    private lateinit var mInputViewHandler: InputViewHandler
    private var showPicker = true

    var text: String
        get() = editText.text.toString()
        set(value) {
            editText.setText(value)
        }

    init {
        editText.id = id
        context.obtainStyledAttributes(attrs, R.styleable.InputView).use {
            mState = it.getInputType(R.styleable.InputView_inputViewState, InputViewState.EMAIL)
            isShowingLabel = it.getBoolean(R.styleable.InputView_showLabel, true)
            update()
        }
    }

    private fun update() {
        layoutInput.apply {
            isHintEnabled = isShowingLabel
            hint = context.getString(mState.placeholder)
            if (mState == InputViewState.PASSWORD || mState == InputViewState.CONFIRM_PASSWORD) {
                editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                endIconDrawable =
                    ContextCompat.getDrawable(context, R.drawable.ic_password_toggle_drawable)
            }
        }

        editText.let {
            mState.type.sum().let { type ->
                it.inputType = type
                if (type == InputType.TYPE_NULL) editText.isFocusableInTouchMode = false
            }
            mInputViewHandler = InputViewHandler(it, mState)
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                mInputViewHandler.textFieldDidEndEditing(text)
            }
        }
    }

    fun showDatePicker(fragment: Fragment) {
        editText.action {
            if (showPicker) {
                showPicker = false
                val initialDate = if (text.isEmpty()) {
                    mState.date
                } else {
                    text.date(format = mState.dateFormat, zone = TimeZone.getDefault())
                }
                val model = DatePickerModel(
                    initialDate = initialDate,
                    minimumDate = mState.minimumDate,
                    maximumDate = mState.maximumDate
                )
                model.setOnDateSelectedListener { date -> mInputViewHandler.updateDate(date) }
                model.setOnDismissedListener { showPicker = true }
                NavigationManager.shared.present(fragment, R.id.date_picker_fragment, model)
            }
        }
    }

    fun setInputViewListener(inputViewListener: InputViewListener) {
        mInputViewHandler.setHandlerListener(inputViewListener)
    }

    fun setInputViewState(newState: InputViewState) {
        mState = newState
        mInputViewHandler.setHandlerState(newState)
        layoutInput.hint = context.getString(newState.placeholder)
        update()
    }

    fun setEmailInputViewState(state: EmailViewState) {
        state.contentType.let { newState ->
            mState = newState
            mInputViewHandler.setHandlerState(newState)
            update()
        }

        //  TODO: Possibly set hintTextAppearance
        layoutInput.apply {
            hint = context.getString(state.title)
            placeholderText = context.getString(state.placeholder)
            isExpandedHintEnabled = false
        }
    }

    fun dismiss() {
        editText.dismiss()
    }

    private val editText
        get() = binding.editText
    private val layoutInput
        get() = binding.layoutInput
}
