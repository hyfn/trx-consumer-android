package com.trx.consumer.models.common

import com.trx.consumer.extensions.format
import org.json.JSONObject
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class UserModel(
    val uid: String = "",
    var birthday: String = "",
    val card: CardModel? = null,
    val cardPaymentMethodId: String? = null,
    var email: String = "",
    var password: String = "",
    val plans: HashMap<String, UserPlanModel> = hashMapOf(),
    var zipCode: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var iap: String = "",
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
        get() {
            val periodInt = plans.values.firstOrNull()?.currentPeriodEnd?.toInt() ?: 0
            val calendar = Calendar.getInstance().apply {
                set(1970, 0, 1, 0, 0, 0)
                add(Calendar.SECOND, periodInt)
            }
            return calendar.time
        }

    private val planStartDate: Date?
        get() {
            val periodInt = plans.values.firstOrNull()?.currentPeriodStart?.toInt() ?: 0
            val calendar = Calendar.getInstance().apply {
                set(1970, 0, 1, 0, 0, 0)
                add(Calendar.SECOND, periodInt)
            }
            return calendar.time
        }

    val planRenewsDateDisplay: String?
        get() =
            plans.values.firstOrNull()?.let {
                if (!it.cancelAtPeriodEnd) {
                    planRenewsDate?.format(format = "MM/dd/YYYY", zone = TimeZone.getDefault())
                } else null
            }

    val planStartDateDisplay: String?
        get() =
            plans.values.firstOrNull()?.let {
                if (!it.cancelAtPeriodEnd) {
                    planStartDate?.format(format = "MM/dd/YYYY", zone = TimeZone.getDefault())
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
            val plans = hashMapOf<String, UserPlanModel>()
            jsonObject.optJSONObject("subscriptions")?.let { plansObject ->
                plansObject.keys().forEach { key ->
                    plans[key] = UserPlanModel.parse(plansObject.getJSONObject(key))
                }
            }
            return UserModel(
                uid = jsonObject.optString("uid"),
                birthday = jsonObject.optString("birthday"),
                email = jsonObject.optString("email"),
                zipCode = jsonObject.optString("postalCode"),
                firstName = jsonObject.optString("firstName"),
                lastName = jsonObject.optString("lastName"),
                iap = jsonObject.optString("iap"),
                plans = plans
            )
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
