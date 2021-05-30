package com.trx.consumer.models.common

import com.trx.consumer.extensions.format
import com.trx.consumer.managers.LogManager
import org.json.JSONObject
import java.util.Date
import java.util.TimeZone
import kotlin.math.roundToLong

class UserModel(
    val uid: String = "",
    var birthday: String = "",
    val card: CardModel? = null,
    val cardPaymentMethodId: String? = null,
    var email: String = "",
    var password: String = "",
    var plans: HashMap<String, UserPlanModel> = hashMapOf(),
    var zipCode: String = "",
    var firstName: String = "",
    var lastName: String = "",
) {

    val fullName: String
        get() = "$firstName $lastName"

    //  TODO: Add correct logic
    val plan: String?
        get() = if (!planIsCancelled) {
            plans.keys.firstOrNull()
        } else null

    private val planIsCancelled: Boolean
        get() {
            return plans.keys.firstOrNull()?.let { key ->
                plans[key]?.cancelAtPeriodEnd ?: false
            } ?: false
        }

    private val planRenewsDate: Date?
        get() = plans.values.firstOrNull()?.let {
            Date(it.currentPeriodEnd.roundToLong())
        }

    val planRenewsDateDisplay: String?
        get() =
            plans.values.firstOrNull()?.let {
                if (!it.cancelAtPeriodEnd) {
                    planRenewsDate?.format(format = "MM/dd/YYYY", zone = TimeZone.getDefault())
                } else null
            }

    val planText: String
        get() = if (!planIsCancelled && plans.keys.contains("UNLIMITED")) {
            kPlanNameUnlimited
        } else kPlanNamePay

    companion object {

        const val kPlanNamePay = "Pay As You Go"
        const val kPlanNameUnlimited = "Unlimited LIVE Classes"

        fun parse(jsonObject: JSONObject): UserModel {

            return UserModel(
                uid = jsonObject.optString("uid"),
                birthday = jsonObject.optString("birthday"),
                email = jsonObject.optString("email"),
                zipCode = jsonObject.optString("postalCode"),
                firstName = jsonObject.optString("firstName"),
                lastName = jsonObject.optString("lastName")
            ).apply {
                try {
                    jsonObject.optJSONObject("subscriptions")?.let { plansJSONObject ->
                        plansJSONObject.keys().forEach { key ->
                            val userPlansJSONObject = plansJSONObject.get(key) as JSONObject
                            userPlansJSONObject
                                .optJSONObject("subscription")?.let { subJSONObject ->
                                    UserPlanModel.parse(subJSONObject)
                                }?.let { userPlans ->
                                    plans[key] = userPlans
                                }
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
