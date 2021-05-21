package com.trx.consumer.models.responses

import com.trx.consumer.models.common.SubscriptionsModel
import org.json.JSONObject

class PlansResponseModel(val subscription: SubscriptionsModel) {

    companion object {

        fun parse(json: String): SubscriptionsModel {
            val jsonObject = JSONObject(json)
                .getJSONObject("data")
                .getJSONObject("customer")
            return SubscriptionsModel.parse(jsonObject)
        }
    }
}
