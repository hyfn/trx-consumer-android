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
    var valueProps: List<String> = listOf(),
    var price: Double = 0.0,
    var primaryState: PlansViewState = PlansViewState.OTHER
) : Parcelable {

    val cost: String
        get() = if (price != 0.0) price.toPrice() else ""

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

        fun test(): PlanModel {
            return PlanModel(
                key = "",
                title = "Unlimited live classes",
                valueProps = listOf(
                    "unlimited lIVE classes",
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
}
