package com.trx.consumer.stripe

import org.json.JSONObject

class StripeCreatePaymentMethodResponseModel(val id: String) {

    companion object {

        fun parse(json: String): StripeCreatePaymentMethodResponseModel {
            val jsonObject = JSONObject(json)
            val id = jsonObject.optString("id")
            return StripeCreatePaymentMethodResponseModel(id)
        }
    }
}
