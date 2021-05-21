package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.WorkoutModel
import org.json.JSONObject

class SessionsResponseModel(val sessions: List<WorkoutModel>) {

    companion object {
        fun parse(json: String): SessionsResponseModel {
            val jsonObject = JSONObject(json)
            val sessions = jsonObject.optJSONArray("data").map { WorkoutModel.parse(it) }
            return SessionsResponseModel(sessions)
        }
    }
}
