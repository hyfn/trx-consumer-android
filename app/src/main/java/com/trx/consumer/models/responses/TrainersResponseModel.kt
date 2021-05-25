package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.TrainerModel
import org.json.JSONObject

class TrainersResponseModel(private val trainers: List<TrainerModel>) {

    val listFeaturedTrainers: List<TrainerModel>
        get() = trainers.filter { it.isFeatured }

    val listLiveTrainers: List<TrainerModel>
        get() = trainers.filter { it.isLive }

    companion object {

        fun parse(json: String): TrainersResponseModel {
            val jsonObject = JSONObject(json)
            val trainers = jsonObject.optJSONArray("data").map { TrainerModel.parse(it) }
            return TrainersResponseModel(trainers)
        }
    }
}
