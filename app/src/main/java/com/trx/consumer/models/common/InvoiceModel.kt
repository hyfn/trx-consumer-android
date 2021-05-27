package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class InvoiceModel(var id: String) : Parcelable {
    companion object {
        fun parse(jsonObject: JSONObject): InvoiceModel =
            InvoiceModel(jsonObject.optString("id"))
    }
}
