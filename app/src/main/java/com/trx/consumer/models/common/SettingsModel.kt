package com.trx.consumer.models.common

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.BuildConfig
import com.trx.consumer.BuildConfig.isVersion1Enabled
import com.trx.consumer.BuildConfig.isVersion2Enabled
import com.trx.consumer.R

class SettingsModel {

    var user: UserModel? = null
    var type: SettingsType = SettingsType.MEMBERSHIPS

    val title: Int
        get() = type.title

    val subtitle: String
        get() =
            when (type) {
                SettingsType.MEMBERSHIPS -> {
                    val size = user?.plans?.size ?: 0
                    "$size active membership${if (size == 1) "" else "s"}"
                }
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
            SettingsType.MEMBERSHIPS -> 10
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
                if (isVersion1Enabled) {
                    add(create(user, SettingsType.MEMBERSHIPS))
                    add(0)
                    add(create(null, SettingsType.SHOP))
                    add(create(null, SettingsType.GETTING_STARTED))
                    add(create(null, SettingsType.CONTACT_SUPPORT))
                    add(create(null, SettingsType.TERMS_AND_CONDITIONS))
                    add(0)
                    add(create(null, SettingsType.LOGOUT))
                } else {
                    add(create(null, SettingsType.MEMBERSHIPS))
                    add(0)
                    add(create(null, SettingsType.SHOP))
                    add(create(null, SettingsType.GETTING_STARTED))
                    add(create(null, SettingsType.CONTACT_SUPPORT))
                    add(create(null, SettingsType.TERMS_AND_CONDITIONS))
                    add(0)
                    add(create(null, SettingsType.LOGOUT))
                }
                if (BuildConfig.DEBUG) add(create(null, SettingsType.TEST_SCREENS))
            }
        }
    }
}

enum class SettingsType {
    SHOP,
    GETTING_STARTED,
    CONTACT_SUPPORT,
    TERMS_AND_CONDITIONS,
    RESTORE,
    LOGOUT,
    TEST_SCREENS,
    MEMBERSHIPS;

    @get:StringRes
    val title: Int
        get() = when (this) {
            SHOP -> R.string.settings_shop
            GETTING_STARTED -> R.string.settings_getting_started
            TERMS_AND_CONDITIONS -> R.string.settings_terms_and_conditions
            CONTACT_SUPPORT -> R.string.settings_contact_support
            RESTORE -> R.string.settings_restore_purchases
            LOGOUT -> R.string.settings_logout
            TEST_SCREENS -> R.string.settings_test_screen
            MEMBERSHIPS -> R.string.settings_membership
        }
}
