package com.trx.consumer.models.common

import com.trx.consumer.extensions.format
import com.trx.consumer.managers.LogManager
import org.json.JSONObject
import java.util.Date
import kotlin.math.roundToLong

class UserModel(
    var birthday: String = "",
    val card: CardModel? = null,
    val cardPaymentMethod: String? = null,
    var email: String = "",
    var password: String = "",
    var subscriptions: HashMap<String, UserSubscriptionModel> = hashMapOf(),
    var zipCode: String = "",
    var firstName: String = "",
    var lastName: String = "",
) {

    val fullName: String
        get() = "$firstName $lastName"

    //  TODO: Add correct logic
    val subscription: String?
        get() = if (!subscriptionIsCancelled) {
            subscriptions.keys.firstOrNull()
        } else null

    val subscriptionIsCancelled: Boolean
        get() {
            return subscriptions.keys.firstOrNull()?.let { key ->
                subscriptions[key]?.cancelAtPeriodEnd ?: false
            } ?: false
        }

    val subRenewsDate: Date?
        get() = subscriptions.values.firstOrNull()?.let {
            Date(it.currentPeriodEnd.roundToLong())
        }

    val subscriptionRenewsDateDisplay: String?
        get() =
            subscriptions.values.firstOrNull()?.let {
                if (!it.cancelAtPeriodEnd) {
                    subRenewsDate?.format(format = "MM/dd/YYY")
                } else null
            }

    val subscriptionText: String
        get() = if (!subscriptionIsCancelled && subscriptions.keys.contains("UNLIMITED")) {
            kSubscriptionNameUnlimited
        } else kSubscriptionNamePay

    companion object {

        const val kSubscriptionNamePay = "Pay As You Go"
        const val kSubscriptionNameUnlimited = "Unlimited LIVE Classes"

        fun parse(jsonObject: JSONObject): UserModel {

            return UserModel(
                birthday = jsonObject.optString("birthday"),
                email = jsonObject.optString("email"),
                zipCode = jsonObject.optString("postalCode"),
                firstName = jsonObject.optString("firstName"),
                lastName = jsonObject.optString("lastName")
            ).apply {
                try {
                    jsonObject.getJSONObject("subscriptions").let { subsJSONObject ->
                        //  TODO: Remove after testing.
                        // val keys = subsJSONObject.keys()
                        // while (keys.hasNext()) {
                        //     val key = keys.next()
                        //     val userSubsJSONObject = subsJSONObject.get(key) as JSONObject
                        //     val userSubs = UserSubscriptionModel.parse(userSubsJSONObject)
                        //     subscriptions[key] = userSubs
                        // }

                        subsJSONObject.keys().forEach { key ->
                            val userSubsJSONObject = subsJSONObject.get(key) as JSONObject
                            val userSubs = UserSubscriptionModel.parse(userSubsJSONObject)
                            subscriptions[key] = userSubs
                        }
                    }
                } catch (e: Exception) {
                    LogManager.log(e)
                }
            }
        }

        fun test(): UserModel {
            return UserModel(
                email = "myo+xpdemoreview1@sprintfwd.com",
                firstName = "Myo",
                lastName = "Kyaw",
                zipCode = "90036",
            )
        }
    }
}
