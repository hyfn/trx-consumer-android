package com.trx.consumer.models.common

import android.content.Context
import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.text.color
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.screens.plans.list.PlansListViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Date
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
    var renewsAtDate: Date? = null,
    var contractUrl: String = ""
) : Parcelable {

    private val days: Int
        get() {
            renewsAtDate?.let {
                // TODO: Insert proper logic

                //  return date.daysUntil
                return 21
            } ?: run {
                return 0
            }
            return 21
        }

    var state: PlansListViewState = PlansListViewState.UNSELECTED

    val creditsText: String
        get() = "$credits Credit${if (credits == 1) "" else "s"}"

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

    private val renewGenericText: SpannableStringBuilder
        get() {
            return if (isRecurring) {
                if (interval == "month") {
                    SpannableStringBuilder("Renews monthly")
                } else {
                    SpannableStringBuilder("Renews every $intervalCount $intervalPlural")
                }
            } else {
                SpannableStringBuilder("One-Time Purchase")
            }
        }

    private fun renewInText(context: Context): SpannableStringBuilder {
        return SpannableStringBuilder("Renews in ")
            .color(
                ContextCompat.getColor(
                    context,
                    R.color.blue
                )
            ) { append("$days days") }
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
                contractUrl = BuildConfig.kWaiversUrl
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
