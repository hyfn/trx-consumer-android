package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.BuildConfig
import com.trx.consumer.screens.plans.list.PlansListViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

@Parcelize
class PlansListModel(
    var credits: Int = 0,
    var description: String = "",
    var interval: String = "",
    var intervalCount: Int = 0,
    var isMembership: Boolean = false,
    var isRecurring: Boolean = false,
    var packageId: String = "",
    var price: Double = 0.0,
    var contractUrl: String = ""
) : Parcelable {

    var state: PlansListViewState = PlansListViewState.UNSELECTED

    private val intervalPlural: String
        get() =
            when (interval) {
                "month" -> interval + "s"
                "day" -> interval + "s"
                else -> "$interval(s)"
            }

    private val intervalText: String
        get() =
            when (interval) {
                "month" -> "mo"
                "day" -> "$intervalCount $intervalPlural"
                else -> interval
            }

    val priceText: String
        get() {
            val formattedPrice = NumberFormat.getCurrencyInstance(
                Locale(Locale.US.language, Locale.US.country)
            ).format(price)
            return if (isRecurring) "$formattedPrice/$intervalText" else formattedPrice
        }

    companion object {

        fun parse(jsonObject: JSONObject): PlansListModel {
            return PlansListModel().apply {
                credits = jsonObject.optInt("credit_count")
                description = jsonObject.optString("description")
                interval = jsonObject.optString("interval")
                intervalCount = jsonObject.optInt("interval_count")
                isMembership = jsonObject.optBoolean("is_membership")
                isRecurring = jsonObject.optBoolean("is_recurring")
                packageId = jsonObject.optString("id")
                price = jsonObject.optJSONObject("price").optDouble("numeric")
                contractUrl = jsonObject.optString("contract_url")
            }
        }

        fun test(): PlansListModel {
            return PlansListModel().apply {
                credits = 50
                price = 49.0
                description = "Typically covers 4-6 classes."
                contractUrl = BuildConfig.trxUrl
            }
        }

        fun testList(count: Int): List<PlansListModel> {
            return mutableListOf<PlansListModel>().apply {
                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
        }
    }
}
