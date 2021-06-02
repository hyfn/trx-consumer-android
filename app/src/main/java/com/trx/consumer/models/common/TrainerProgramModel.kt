package com.trx.consumer.models.common

import com.trx.consumer.extensions.upperCased
import com.trx.consumer.models.states.WorkoutViewState
import org.json.JSONObject

class TrainerProgramModel {
    var durationInMinutes: Int = 0
    var isPublic: Boolean = false
    var key: String = ""
    var mode: String = ""
    var name: String = ""
    var priceInCents: Int = 0
    var trainer: TrainerModel = TrainerModel()

    // MARK: - Test
    companion object {
        fun parse(jsonObject: JSONObject) = TrainerProgramModel().apply {
            durationInMinutes = jsonObject.optInt("durationInMinutes")
            isPublic = jsonObject.optBoolean("public")
            key = jsonObject.optString("key")
            mode = jsonObject.optString("mode")
            name = jsonObject.optString("name")
            priceInCents = jsonObject.optInt("priceInCents")
            trainer = jsonObject.optJSONObject("trainer")?.let { TrainerModel.parse(it) }
                ?: TrainerModel()
        }

        val test: TrainerProgramModel
            get() = TrainerProgramModel().apply {
                durationInMinutes = 30
                isPublic = true
                key = "-MXj8znm1e8OWB4y4NLY"
                mode = WorkoutViewState.SMALL_GROUP_MODE
                name = "1:1 Virtual Personal Training"
                priceInCents = 1000
                trainer = TrainerModel.test()
            }
    }

    val image: String
        get() = when (mode) {
            WorkoutViewState.SMALL_GROUP_MODE -> "img_virtual_small_group"
            WorkoutViewState.ONE_ON_ONE_MODE -> "img_virtual_one_on_one"
            else -> ""
        }

    val subtitle: String
        get() = "$durationDisplay   $priceDisplay"

    val durationDisplay: String
        get() = "$durationInMinutes min".upperCased()

    val priceCurrency: String
        get() = "$"

    val priceDisplay: String
        get() = "$priceCurrency ${priceInCents / 100}"
}
