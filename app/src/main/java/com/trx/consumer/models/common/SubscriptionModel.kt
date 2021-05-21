package com.trx.consumer.models.common

import com.trx.consumer.extensions.map
import com.trx.consumer.extensions.toPrice
import org.json.JSONObject

class SubscriptionModel(
    var key: String = "",
    var price: Double = 0.0,
    var title: String = "",
    var valueProps: List<String> = listOf()
) {

    val priceDisplay: String
        get() = price.toPrice()

    val plan: PlanModel
        get() = PlanModel(
            key = key,
            title = title,
            cost = priceDisplay,
            description = valueProps.joinToString(separator = "\n")
        )

    companion object {

        fun parse(jsonObject: JSONObject): SubscriptionModel {
            return SubscriptionModel(
                key = jsonObject.optString("key", ""),
                price = jsonObject.optDouble("price", 0.0),
                title = jsonObject.optString("title", ""),
                valueProps = jsonObject.optJSONArray("valueProps")?.let { jsonArray ->
                    jsonArray.map { it.toString() }
                } ?: listOf()
            )
        }
    }
}
