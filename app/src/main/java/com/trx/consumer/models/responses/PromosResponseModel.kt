package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.PromoModel
import org.json.JSONObject

class PromosResponseModel(val promos: List<PromoModel>) {

    companion object {
        fun parse(json: String): PromosResponseModel {
            val jsonObject = JSONObject(json)
            val sessions = jsonObject.optJSONArray("data").map { PromoModel.parse(it) }
            return PromosResponseModel(sessions)
        }
    }
}
