package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.screens.email.EmailViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmailParamsModel(
    val state: EmailViewState,
    val email: String = ""
) : Parcelable
