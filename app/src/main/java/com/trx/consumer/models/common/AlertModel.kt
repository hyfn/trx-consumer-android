package com.trx.consumer.models.common

import android.os.Parcel
import android.os.Parcelable
import android.text.SpannableString
import android.text.TextUtils
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.screens.alert.AlertBackAction
import com.trx.consumer.screens.alert.AlertViewState
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class AlertModel private constructor(
    var title: String = "",
    var message: String = "",
    var primaryMethod: (() -> Unit)? = null,
    @StringRes
    var primaryTitle: Int = R.string.alert_button_title_dismiss,
    var primaryState: AlertViewState = AlertViewState.NEUTRAL,
    var secondaryMethod: (() -> Unit)? = null,
    @StringRes
    var secondaryTitle: Int = R.string.alert_button_title_dismiss,
    var clearTitle: @RawValue SpannableString? = null,
    var secondaryState: AlertViewState = AlertViewState.NEUTRAL,
    var backPressAction: AlertBackAction? = null
) : Parcelable {

    fun setPrimaryButton(
        title: Int,
        state: AlertViewState = AlertViewState.NEUTRAL,
        method: (() -> Unit)? = null
    ) {
        primaryTitle = title
        primaryState = state
        primaryMethod = method
    }

    fun setSecondaryButton(
        title: Int,
        state: AlertViewState = AlertViewState.NEUTRAL,
        method: (() -> Unit)? = null
    ) {
        secondaryTitle = title
        secondaryState = state
        secondaryMethod = method
    }

    fun setClearButton(
        title: SpannableString,
        state: AlertViewState = AlertViewState.CLEAR,
        method: (() -> Unit)? = null
    ) {
        clearTitle = title
        secondaryState = state
        secondaryMethod = method
    }

    fun setBackAction(alertBackAction: AlertBackAction) {
        backPressAction = alertBackAction
    }

    companion object : Parceler<AlertModel> {

        fun create(title: String, message: String): AlertModel =
            AlertModel().apply {
                this.title = title
                this.message = message
            }

        override fun AlertModel.write(dest: Parcel, flags: Int) {
            TextUtils.writeToParcel(clearTitle, dest, flags)
        }

        override fun create(parcel: Parcel): AlertModel = AlertModel(clearTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel) as @RawValue SpannableString)
    }
}
