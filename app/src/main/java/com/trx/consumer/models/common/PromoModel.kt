package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class PromoModel(
    var identifier: String,
    var ctaHref: String,
    var title: String,
    var imageUrl: String
) : Parcelable {

    companion object {

        fun parse(jsonObject: JSONObject): PromoModel {
            return PromoModel(
                identifier = jsonObject.optString("productId"),
                ctaHref = jsonObject.optString("ctaHref"),
                title = jsonObject.optString("whiteText"),
                imageUrl = jsonObject.optString("imageUrl")
            )
        }

        fun test(): PromoModel {
            return PromoModel(
                identifier = "123",
                ctaHref = "https://store.trxtraining.com/products/trx-pro/",
                title = "Get 50% off your first Live session",
                imageUrl = "https://trx.sprintfwd.com/banner-images/trx-pro4-system.jpeg"
            )
        }

        fun testList(count: Int): List<PromoModel> {
            return (0 until count).map { test() }
        }
    }
}
