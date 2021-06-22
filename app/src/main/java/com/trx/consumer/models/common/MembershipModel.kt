package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.format
import com.trx.consumer.extensions.map
import com.trx.consumer.extensions.toPrice
import com.trx.consumer.screens.memberships.list.MembershipViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.Date
import java.util.TimeZone

@Parcelize
class MembershipModel(
    var key: String = "",
    val valueProps: List<String> = listOf(),
    val priceInCents: Int = 0,
    var primaryState: MembershipViewState = MembershipViewState.BASE,
    val userType: String = "",
    val customerFacingName: String = "",
    val promoTitle: String = "",
    val promoDescription: String = "",
    val billingDescription: String = "",
    val showInMobile: Boolean = false,
    val revcatProductId: String = "",
    val entitlements: EntitlementsModel = EntitlementsModel(),
    var currentPeriodEnd: Long = 0,
    var currentPeriodStart: Long = 0
) : Parcelable {

    val cost: String
        get() = "${(priceInCents / 100.0).toPrice()} per Month"

    val description: String
        get() = valueProps.joinToString(separator = "\n")

    val lastBillDate: String
        get() = Date(currentPeriodStart * 1000).format("MM/dd/YYYY", zone = TimeZone.getDefault())

    val nextBillDate: String
        get() = Date(currentPeriodEnd * 1000).format("MM/dd/YYYY", zone = TimeZone.getDefault())

    companion object {

        fun parse(jsonObject: JSONObject): MembershipModel {
            val productId = jsonObject.optJSONArray("revcatProductIds").map {
                RevcatProductModel.parse(it)
            }.firstOrNull { it.platform == "android" }?.productId ?: ""
            return MembershipModel(
                key = jsonObject.optString("key", ""),
                valueProps = jsonObject.optJSONArray("valueProps").map(),
                priceInCents = jsonObject.optInt("priceInCents"),
                userType = jsonObject.optString("userType"),
                customerFacingName = jsonObject.optString("customerFacingName"),
                promoTitle = jsonObject.optString("promoTitle"),
                promoDescription = jsonObject.optString("promoDescription"),
                billingDescription = jsonObject.optString("billingDescription"),
                showInMobile = jsonObject.optBoolean("showInMobile"),
                revcatProductId = productId,
                entitlements = jsonObject.optJSONObject("permissions")?.let {
                    EntitlementsModel.parse(it)
                } ?: EntitlementsModel()
            )
        }

        fun test(): MembershipModel {
            return MembershipModel(
                key = "",
                customerFacingName = "Unlimited live classes",
                valueProps = listOf(
                    "Unlimited LIVE classes",
                    "Hundreds of on-demand videos ",
                    "free virtual training intro sessions. ",
                    "discounts on TRX products"
                ),
                priceInCents = 1999,
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
