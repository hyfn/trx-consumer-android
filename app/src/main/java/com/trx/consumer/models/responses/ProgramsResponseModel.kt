package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.TrainerProgramModel
import org.json.JSONObject

class ProgramsResponseModel {
    private var programs: List<TrainerProgramModel> = listOf()

    companion object {
        fun parse(json: String) = ProgramsResponseModel().apply {
            val jsonObject = JSONObject(json)
            programs = jsonObject.optJSONArray("data").map { TrainerProgramModel.parse(it) }
        }
    }

    val lstPrograms: List<TrainerProgramModel>
        get() = programs.filter { it.isPublic }
}
