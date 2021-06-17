package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class RevcatProductModel(val platform: String = "", val productId: String  = ""): Parcelable {

    companion object {

        fun parse(jsonObject: JSONObject): RevcatProductModel {
            return RevcatProductModel(
                platform = jsonObject.getString("platform"),
                productId = jsonObject.getString("productId")
            )
        }
    }
}