package com.trx.consumer.models.common

import com.trx.consumer.extensions.map
import com.trx.consumer.managers.LogManager
import org.json.JSONObject
import java.lang.Exception

class SubscriptionsModel(
    var free: SubscriptionModel = SubscriptionModel(),
    var plans: List<SubscriptionModel> = listOf()
) {

    companion object {

        fun parse(jsonObject: JSONObject): SubscriptionsModel =
            SubscriptionsModel(
                free = jsonObject.optJSONObject("free")?.let {
                    SubscriptionModel.parse(it)
                } ?: SubscriptionModel(),
                plans = try {
                    jsonObject.getJSONArray("plans").let { jsonArray ->
                        jsonArray.map { SubscriptionModel.parse(it) }
                    }
                } catch (e: Exception) {
                    LogManager.log(e)
                    listOf()
                }
            )
    }
}
