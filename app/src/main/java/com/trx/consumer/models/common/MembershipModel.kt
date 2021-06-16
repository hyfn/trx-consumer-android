package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.map
import com.trx.consumer.extensions.toPrice
import com.trx.consumer.screens.memberships.list.MembershipViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class MembershipModel(
    var key: String = "",
    var title: String = "",
    var valueProps: List<String> = listOf(),
    var price: Double = 0.0,
    var primaryState: MembershipViewState = MembershipViewState.PLAIN
) : Parcelable {

    val cost: String
        get() = if (price != 0.0) "${(price / 100.0).toPrice()} per Month" else ""

    val description: String
        get() = valueProps.joinToString(separator = "\n")

    companion object {

        fun parse(jsonObject: JSONObject): MembershipModel {
            return MembershipModel(
                key = jsonObject.optString("key", ""),
                price = jsonObject.optDouble("price", 0.0),
                title = jsonObject.optString("title", ""),
                valueProps = jsonObject.optJSONArray("valueProps").map()
            )
        }

        fun test(): MembershipModel {
            return MembershipModel(
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

        fun testList(count: Int): List<MembershipModel> {
            return mutableListOf<MembershipModel>().apply {
                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
        }
    }
}
