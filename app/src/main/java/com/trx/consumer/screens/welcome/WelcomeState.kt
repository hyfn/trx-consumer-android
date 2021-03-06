package com.trx.consumer.screens.welcome

import androidx.annotation.StringRes
import com.trx.consumer.R

enum class WelcomeState {
    WELCOME, PROMO;

    @get:StringRes
    val buttonTitle: Int
        get() = when (this) {
            WELCOME -> R.string.welcome_state_welcome_button_label
            PROMO -> R.string.welcome_state_promo_button_label
        }

    @get:StringRes
    val title: Int
        get() = when (this) {
            WELCOME -> R.string.welcome_state_welcome_title
            PROMO -> R.string.welcome_state_promo_title
        }

    @get:StringRes
    val description: Int
        get() = when (this) {
            WELCOME -> R.string.welcome_state_welcome_description
            PROMO -> R.string.welcome_state_promo_description
        }
}
