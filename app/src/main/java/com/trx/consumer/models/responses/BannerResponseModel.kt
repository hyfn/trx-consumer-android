package com.trx.consumer.models.responses

import com.trx.consumer.models.common.BannerModel
import org.json.JSONObject

class BannerResponseModel(var banner: BannerModel) {

    companion object {
        fun parse(json: String): BannerResponseModel {
            val jsonObject = JSONObject(json)
            val bannerObject = jsonObject.optJSONObject("data")
            val banner = bannerObject?.let { BannerModel.parse(bannerObject) } ?: BannerModel()
            return BannerResponseModel(banner)
        }
    }
}
