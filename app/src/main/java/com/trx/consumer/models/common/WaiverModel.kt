package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class WaiverModel(var covid: String = "", var terms: String = "") : Parcelable {
    companion object {
        fun parse(jsonObject: JSONObject): WaiverModel {
            return WaiverModel().apply {
                covid = jsonObject.optString("covid")
                terms = jsonObject.optString("terms")
            }
        }
    }
}
