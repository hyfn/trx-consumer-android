package com.trx.consumer.screens.loading

import com.trx.consumer.R

enum class LoadingViewState {
    LAUNCH;

    val message: Int
        get() = when (this) {
            LAUNCH -> R.string.title_loading_fragment
        }
}
