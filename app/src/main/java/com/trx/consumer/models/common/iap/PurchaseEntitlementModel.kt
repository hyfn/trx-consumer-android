package com.trx.consumer.models.common.iap

import com.trx.consumer.BuildConfig
import com.trx.consumer.extensions.date
import com.trx.consumer.extensions.format
import org.json.JSONObject
import java.util.Date
import java.util.TimeZone

class PurchaseEntitlementModel(
    val expiresDateString: String = "",
    val productIdentifier: String = "",
    val purchaseDateString: String = ""
) {

    private val expiresDate: Date?
        get() = date(expiresDateString)

    val expiresDisplay: String
        get() = display(expiresDate)

    private fun date(string: String): Date? {
        return string.date(format = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    val isValid: Boolean
        get() = expiresDate?.after(Date()) ?: false

    val purchaseDisplay: String
        get() {
            val date = date(purchaseDateString)
            return display(date)
        }

    private fun display(date: Date?): String {
        return if (BuildConfig.DEBUG) {
            date?.format("yyyy/MM/dd h:mm a", zone = TimeZone.getDefault()) ?: ""
        } else {
            date?.format("yyyy/MM/dd", zone = TimeZone.getDefault()) ?: ""
        }
    }

    companion object {

        fun parse(jsonObject: JSONObject): PurchaseEntitlementModel {
            return PurchaseEntitlementModel(
                expiresDateString = jsonObject.optString("expires_date"),
                productIdentifier = jsonObject.optString("product_identifier"),
                purchaseDateString = jsonObject.optString("purchase_date")
            )
        }
    }
}
