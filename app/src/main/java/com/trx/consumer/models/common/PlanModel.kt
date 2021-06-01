package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.map
import com.trx.consumer.extensions.toPrice
import com.trx.consumer.screens.plans.list.PlansViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class PlanModel(
    var key: String = "",
    var title: String = "",
    var trialDays: Int = 0,
    var permissions: PlanPermissionModel = PlanPermissionModel(),
    var price: Double = 0.0,
    var creditsPerMonth: Int = 0,
    var conflicts: List<PlanType> = listOf(),
    var subsToHideWhenSubscribed: List<PlanType> = listOf(),
    val userType: String = "",
    var valueProps: List<String> = listOf(),
    var primaryState: PlansViewState = PlansViewState.OTHER,
    var hideWhenNotSubscribed: Boolean = false
) : Parcelable {

    val cost: String
        get() = if (price != 0.0) "${(price / 100.0).toPrice()} per Month" else ""

    val description: String
        get() = valueProps.joinToString(separator = "\n")

    companion object {

        fun parse(jsonObject: JSONObject): PlanModel {
            return PlanModel(
                key = jsonObject.optString("key", ""),
                price = jsonObject.optDouble("price", 0.0),
                title = jsonObject.optString("title", ""),
                valueProps = jsonObject.optJSONArray("valueProps").map()
            )
        }

        fun parseDevBase(jsonObject: JSONObject): PlanModel {
            return PlanModel(
                key = jsonObject.optString("key"),
                userType = jsonObject.optString("userType"),
                title = jsonObject.optString("name"),
                trialDays = jsonObject.optInt("trialDays"),
                price = jsonObject.optInt("priceInCents").toDouble(),
                permissions = jsonObject.getJSONObject("permissions")
                    .let { PlanPermissionModel.parse(it) },
                conflicts = jsonObject.optJSONArray("subsThisOneConflictsWith")?.let {
                    it.map<String>().map { typeString ->
                        PlanType.from(typeString)
                    }
                } ?: listOf(),
                valueProps = jsonObject.optJSONArray("valueProps").map(),
            )
        }

        fun parseDevCustom(jsonObject: JSONObject): PlanModel {
            return PlanModel(
                key = jsonObject.optString("key", jsonObject.optString("name")),
                title = jsonObject.optString("name"),
                price = jsonObject.optInt("priceInCents").toDouble(),
                creditsPerMonth = jsonObject.optInt("smallGroupLiveClassCreditsPerMonth"),
                conflicts = jsonObject.optJSONArray("subsThisOneConflictsWith")?.let {
                    it.map<String>().map { typeString ->
                        PlanType.from(typeString)
                    }
                } ?: listOf(),
                subsToHideWhenSubscribed = jsonObject
                    .optJSONArray("subsToHideWhenSubscribed")?.let {
                        it.map<String>().map { typeString ->
                            PlanType.from(typeString)
                        }
                    } ?: listOf(),
                userType = jsonObject.optString("userType"),
                permissions = jsonObject
                    .optJSONObject("permissions")
                    .let { PlanPermissionModel.parse(it) },
                valueProps = jsonObject.optJSONArray("valueProps").map(),
                hideWhenNotSubscribed = jsonObject.optBoolean("hideWhenNotSubscribed"),
                trialDays = jsonObject.optInt("trialDays")
            )
        }

        fun test(): PlanModel {
            return PlanModel(
                key = "",
                title = "Unlimited live classes",
                valueProps = listOf(
                    "Unlimited LIVE classes",
                    "Hundreds of on-demand videos ",
                    "free virtual training intro sessions. ",
                    "discounts on TRX products"
                ),
                price = 19.99,
            )
        }

        fun testList(count: Int): List<PlanModel> {
            return mutableListOf<PlanModel>().apply {
                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
        }
    }

    enum class PlanType {
        DICKS,
        CORE,
        UNLIMITED,
        UNKNOWN;

        companion object {
            fun from(plan: String): PlanType {
                return values().firstOrNull { it.name.equals(plan, ignoreCase = true) }
                    ?: UNKNOWN
            }
        }
    }
}
