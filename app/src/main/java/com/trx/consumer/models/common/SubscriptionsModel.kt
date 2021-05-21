package com.trx.consumer.models.common

import com.trx.consumer.extensions.map
import org.json.JSONObject
import java.lang.Exception

class SubscriptionsModel(
    var free: SubscriptionModel = SubscriptionModel(),
    var plans: List<SubscriptionModel> = listOf()
) {

    companion object {

        fun parse(jsonObject: JSONObject): SubscriptionsModel =
            SubscriptionsModel(
                free = try {
                    jsonObject.getJSONObject("free").let {
                        SubscriptionModel.parse(it)
                    }
                } catch (e: Exception) {
                    SubscriptionModel()
                },
                plans = try {
                    jsonObject.getJSONArray("plans").let { jsonArray ->
                        jsonArray.map { SubscriptionModel.parse(it) }
                    }
                } catch (e: Exception) {
                    listOf()
                }
            )
    }
}
