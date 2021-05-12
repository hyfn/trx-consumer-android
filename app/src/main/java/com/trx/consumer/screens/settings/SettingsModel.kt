package com.trx.consumer.screens.settings

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.models.common.UserModel

class SettingsModel {

    var user: UserModel? = null
    var type: SettingsType = SettingsType.EDIT_PROFILE

    val title: Int
        get() = type.title

    val subtitle: String
        get() =
            when (type) {
                SettingsType.SUBSCRIPTIONS -> "ACTIVE | Renews Oct. 29, 2020"
                SettingsType.PAYMENT_METHODS -> {
                    user?.card?.let { _ ->
                        "None"
                    } ?: "Connected"
                }
                SettingsType.NOTIFICATIONS -> "2 Active"
                else -> ""
            }

    @get:ColorRes
    val textColor: Int
        get() = when (type) {
            SettingsType.LOGOUT -> R.color.red
            else -> R.color.blackRussian
        }

    val textSize: Int
        get() = when (type) {
            SettingsType.SUBSCRIPTIONS -> 10
            SettingsType.PAYMENT_METHODS -> 10
            SettingsType.NOTIFICATIONS -> 10
            else -> 16
        }

    companion object {
        fun create(user: UserModel?, type: SettingsType): SettingsModel =
            SettingsModel().apply {
                this.user = user
                this.type = type
            }

        fun list(user: UserModel?): MutableList<Any> {
            return mutableListOf<Any>().apply {
                add(create(null, SettingsType.EDIT_PROFILE))
                add(create(user, SettingsType.SUBSCRIPTIONS))
                add(create(user, SettingsType.PAYMENT_METHODS))
                add(create(user, SettingsType.NOTIFICATIONS))
                add(create(null, SettingsType.SHOP))
                add(create(null, SettingsType.GETTING_STARTED))
                add(create(null, SettingsType.CONTACT_SUPPORT))
                add(create(null, SettingsType.TERMS_AND_CONDITIONS))
                add(create(null, SettingsType.LOGOUT))
            }
        }
    }
}

enum class SettingsType {
    EDIT_PROFILE,
    SUBSCRIPTIONS,
    PAYMENT_METHODS,
    NOTIFICATIONS,
    SHOP,
    GETTING_STARTED,
    TERMS_AND_CONDITIONS,
    CONTACT_SUPPORT,
    WORKOUT_LIVE,
    WORKOUT_VIDEO,
    WORKOUT_VIRTUAL,
    TRAINER,
    ON_BOARDING_VPT,
    WELCOME,
    LOGOUT,
    SCHEDULE_TRAINER,
    SCHEDULE_USER_VIRTUAL;

    @get:StringRes
    val title: Int
        get() = when (this) {
            EDIT_PROFILE -> R.string.settings_edit_profile
            SUBSCRIPTIONS -> R.string.settings_subscriptions
            PAYMENT_METHODS -> R.string.settings_payment_methods
            NOTIFICATIONS -> R.string.settings_notifications
            SHOP -> R.string.settings_shop
            GETTING_STARTED -> R.string.settings_getting_started
            TERMS_AND_CONDITIONS -> R.string.settings_terms_and_conditions
            CONTACT_SUPPORT -> R.string.settings_contact_support
            WORKOUT_LIVE -> R.string.settings_workout_live
            WORKOUT_VIDEO -> R.string.settings_workout_live
            WORKOUT_VIRTUAL -> R.string.settings_workout_virtual
            TRAINER -> R.string.settings_trainer
            ON_BOARDING_VPT -> R.string.settings_on_boarding_vpt
            WELCOME -> R.string.settings_welcome
            LOGOUT -> R.string.settings_logout
            SCHEDULE_TRAINER -> R.string.settings_schedule_trainer
            SCHEDULE_USER_VIRTUAL -> R.string.settings_schedule_user_virtual
        }
}
