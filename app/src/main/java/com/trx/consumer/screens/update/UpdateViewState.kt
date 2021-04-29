package com.trx.consumer.screens.update

import androidx.annotation.StringRes
import com.trx.consumer.R

enum class UpdateViewState {
    CREATE, EDIT;

    @get:StringRes
    val buttonTitle: Int
        get() = when (this) {
            CREATE -> R.string.update_save_label
            EDIT -> R.string.update_save_changes_label
        }
}
