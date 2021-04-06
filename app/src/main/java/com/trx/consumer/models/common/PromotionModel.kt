package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PromotionModel(
    var identifier: String = "",
    var ctaHref: String = "",
    var title: String = "",
    var imageUrl: String = ""
) : Parcelable {

    companion object {

        fun test(): PromotionModel {
            return PromotionModel(
                identifier = "123",
                ctaHref = "https://store.trxtraining.com/products/trx-pro/",
                title = "Get 50% off your first Live session",
                imageUrl = "https://trx.sprintfwd.com/banner-images/trx-pro4-system.jpeg"
            )
        }

        fun testList(count: Int): List<PromotionModel> {
            return (0 until count).map { test() }
        }
    }
}