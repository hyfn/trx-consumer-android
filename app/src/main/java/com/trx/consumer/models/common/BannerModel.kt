package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class BannerModel(
    var isActive: Boolean,
    var modalButtonText: String,
    var modalDescription: String,
    var modalImageUrl: String,
    var modalTitle: String,
    var offerButtonLink: String
) : Parcelable {

    companion object {

        fun parse(jsonObject: JSONObject): BannerModel {
            return BannerModel(
                isActive = jsonObject.optBoolean("active"),
                modalButtonText = jsonObject.optString("modalButtonText"),
                modalDescription = jsonObject.optString("modalDescription"),
                modalImageUrl = jsonObject.optString("modalImageUrl"),
                modalTitle = jsonObject.optString("modalTitle"),
                offerButtonLink = jsonObject.optString("offerButtonLink")
            )
        }
    }
}
