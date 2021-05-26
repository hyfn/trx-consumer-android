package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.InvoiceModel
import org.json.JSONObject

class BookIntentResponseModel(val invoices: List<InvoiceModel>) {

    val invoiceId: String
        get() = invoices.firstOrNull()?.id ?: ""

    companion object {
        fun parse(json: String): BookIntentResponseModel =
            JSONObject(json).optJSONObject("data")?.let {
                it.optJSONArray("invoices").map { invoicesJSONArray ->
                    InvoiceModel.parse(invoicesJSONArray)
                }.let { invoicesList ->
                    BookIntentResponseModel(invoicesList)
                }
            }.let { BookIntentResponseModel(listOf()) }
    }
}
