package com.trx.consumer.screens.settings

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.models.common.UserModel

class SettingsModel {

    var user: UserModel? = null
    var type: SettingsType = SettingsType.PLANS

    val title: Int
        get() = type.title

    val subtitle: String
        get() =
            when (type) {
                // TODO: Display text based on user's subscription
                SettingsType.PLANS -> "ACTIVE | Renews Oct. 29, 2020"
                else -> ""
            }

    @get:ColorRes
    val titleTextColor: Int
        get() = when (type) {
            SettingsType.LOGOUT -> R.color.red
            else -> R.color.black
        }

    val titleTextSize: Int
        get() = when (type) {
            SettingsType.PLANS -> 10
            else -> 16
        }

    companion object {
        fun create(user: UserModel?, type: SettingsType): SettingsModel =
            SettingsModel().apply {
                this.user = user
                this.type = type
            }

        fun list(user: UserModel?): List<Any> {
            return listOf(
                create(user, SettingsType.PLANS),
                0,
                create(null, SettingsType.SHOP),
                create(null, SettingsType.GETTING_STARTED),
                create(null, SettingsType.CONTACT_SUPPORT),
                create(null, SettingsType.TERMS_AND_CONDITIONS),
                0,
                create(null, SettingsType.LOGOUT)
            )
        }
    }
}

enum class SettingsType {
    PLANS,
    SHOP,
    GETTING_STARTED,
    CONTACT_SUPPORT,
    TERMS_AND_CONDITIONS,
    RESTORE,
    LOGOUT;

    @get:StringRes
    val title: Int
        get() = when (this) {
            PLANS -> R.string.settings_plans
            SHOP -> R.string.settings_shop
            GETTING_STARTED -> R.string.settings_getting_started
            TERMS_AND_CONDITIONS -> R.string.settings_terms_and_conditions
            CONTACT_SUPPORT -> R.string.settings_contact_support
            RESTORE -> R.string.settings_restore_purchases
            LOGOUT -> R.string.settings_logout
        }
}
