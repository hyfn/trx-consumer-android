package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class ParticipantModel(
    var firstName: String = "",
    var uid: String = ""
) : Parcelable {
    companion object {
        fun parse(jsonObject: JSONObject): ParticipantModel =
            ParticipantModel(
                firstName = jsonObject.optString("firstName"),
                uid = jsonObject.optString("uid")
            )
    }
}
