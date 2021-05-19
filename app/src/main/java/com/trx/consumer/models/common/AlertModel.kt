package com.trx.consumer.models.common

import android.os.Parcelable
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.screens.alert.AlertBackAction
import com.trx.consumer.screens.alert.AlertViewState
import kotlinx.parcelize.Parcelize

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

    fun setBackAction(alertBackAction: AlertBackAction) {
        backPressAction = alertBackAction
    }

    companion object {

        fun create(title: String, message: String): AlertModel =
            AlertModel().apply {
                this.title = title
                this.message = message
            }
    }
}
