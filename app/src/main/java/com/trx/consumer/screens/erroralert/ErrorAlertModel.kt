package com.trx.consumer.screens.erroralert

import android.os.Parcelable
import com.trx.consumer.R
import kotlinx.parcelize.Parcelize

@Parcelize
class ErrorAlertModel(
    var message: String = "",
    var imgName: Int = R.drawable.ic_img_error
) : Parcelable {
    companion object {
        fun error(message: String): ErrorAlertModel =
            ErrorAlertModel().apply {
                this.message = message
            }
    }
}
