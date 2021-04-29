package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.AccountModel
import com.trx.consumer.screens.update.UpdateViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateParamsModel(
    var state: UpdateViewState,
    var accounts: List<AccountModel>,
    var email: String
) : Parcelable
