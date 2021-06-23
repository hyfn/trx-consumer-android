package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.TrainerProgramModel
import com.trx.consumer.models.common.TrainerScheduleModel
import org.json.JSONObject

class ProgramsAvailabilityResponseModel(var lstTimes: List<Long> = listOf()) {

    companion object {
        fun parse(json: String): ProgramsAvailabilityResponseModel {
            val jsonObject = JSONObject(json)
            val lstTimes = jsonObject.optJSONArray("data").map { it as Long }
            return ProgramsAvailabilityResponseModel(lstTimes)
        }
    }

    fun lstClasses( program : TrainerProgramModel) : List<TrainerScheduleModel> {
        return lstTimes.map { timestamp -> TrainerScheduleModel(program, timestamp) }
    }
}