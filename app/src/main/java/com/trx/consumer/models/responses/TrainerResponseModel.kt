package com.trx.consumer.models.responses

import com.trx.consumer.models.common.TrainerModel
import org.json.JSONObject

class TrainerResponseModel {
    var trainer: TrainerModel = TrainerModel()

    companion object {
        fun parse(json: String) = TrainerResponseModel().apply {
            JSONObject(json).optJSONObject("data")?.let { jsonObject ->
                trainer = TrainerModel.parse(jsonObject)
            }
        }
    }
}