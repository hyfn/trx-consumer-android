package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.screens.workout.WorkoutViewState
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

        fun parse(jsonObject: JSONObject): TrainerProgramModel {
            return TrainerProgramModel().apply {
                durationInMinutes = jsonObject.optInt("durationInMinutes")
                isPublic = jsonObject.optBoolean("public")
                key = jsonObject.optString("key")
                mode = jsonObject.optString("mode")
                name = jsonObject.optString("name")
                priceInCents = jsonObject.optInt("priceInCents")
                trainer = jsonObject.opt("trainer") as TrainerModel
            }
        }

        fun test(): TrainerProgramModel {
            return TrainerProgramModel().apply {
                durationInMinutes = 30
                isPublic = true
                key = "-MXj8znm1e8OWB4y4NLY"
                mode = WorkoutViewState.VIRTUAL.name
                name = "1:1 Virtual Personal Training"
                priceInCents = 1000
                trainer = TrainerModel.test()
            }
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
}
