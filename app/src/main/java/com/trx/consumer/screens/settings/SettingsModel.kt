package com.trx.consumer.screens.settings

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.models.common.UserModel

class SettingsModel {

    var user: UserModel? = null
    var type: SettingsType = SettingsType.SUBSCRIPTIONS

    val title: Int
        get() = type.title

    val subtitle: String
        get() =
            when (type) {
                SettingsType.SUBSCRIPTIONS -> "ACTIVE | Renews Oct. 29, 2020"
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
            SettingsType.SUBSCRIPTIONS -> 10
            else -> 16
        }

    companion object {
        fun create(user: UserModel?, type: SettingsType): SettingsModel =
            SettingsModel().apply {
                this.user = user
                this.type = type
            }

        fun list(user: UserModel?): List<Any> {
            return mutableListOf<Any>().apply {
                add(create(user, SettingsType.SUBSCRIPTIONS))
                add(0)
                add(create(null, SettingsType.SHOP))
                add(create(null, SettingsType.GETTING_STARTED))
                add(create(null, SettingsType.CONTACT_SUPPORT))
                add(create(null, SettingsType.TERMS_AND_CONDITIONS))
                add(0)
                add(create(null, SettingsType.RESTORE_PURCHASES))
                add(0)
                add(create(null, SettingsType.LOGOUT))
            }
        }
    }
}

enum class SettingsType {
    SUBSCRIPTIONS,
    SHOP,
    GETTING_STARTED,
    CONTACT_SUPPORT,
    TERMS_AND_CONDITIONS,
    RESTORE_PURCHASES,
    LOGOUT;

    @get:StringRes
    val title: Int
        get() = when (this) {
            SUBSCRIPTIONS -> R.string.settings_subscriptions
            SHOP -> R.string.settings_shop
            GETTING_STARTED -> R.string.settings_getting_started
            TERMS_AND_CONDITIONS -> R.string.settings_terms_and_conditions
            CONTACT_SUPPORT -> R.string.settings_contact_support
            RESTORE_PURCHASES -> R.string.settings_restore_purchases
            LOGOUT -> R.string.settings_logout
        }
}
