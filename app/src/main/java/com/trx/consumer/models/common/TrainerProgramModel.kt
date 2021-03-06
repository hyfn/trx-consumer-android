package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.upperCased
import com.trx.consumer.models.states.WorkoutViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class TrainerProgramModel(
    var durationInMinutes: Int = 0,
    var isPublic: Boolean = false,
    var key: String = "",
    var mode: String = "",
    var name: String = "",
    var priceInCents: Int = 0,
    var trainer: TrainerModel = TrainerModel()
) : Parcelable {

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

        fun test(): TrainerProgramModel = TrainerProgramModel().apply {
            durationInMinutes = 30
            isPublic = true
            key = "-Md0QA7T56UTtNQZxO0y"
            mode = WorkoutViewState.SMALL_GROUP_MODE
            name = "1:1 Virtual Personal Training"
            priceInCents = 1000
            trainer = TrainerModel.test()
        }

        fun testList(count: Int): List<TrainerProgramModel> {
            return mutableListOf<TrainerProgramModel>().apply {
                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
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
