package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.IAPModel
import com.trx.consumer.models.common.PurchaseEntitlementModel
import com.trx.consumer.models.common.PurchaseModel
import org.json.JSONObject

class PurchasesResponseModel(val purchase: PurchaseModel, val purchases: List<PurchaseModel>) {

    companion object {

        fun parse(json: String): PurchasesResponseModel {
            val jsonObject = JSONObject(json)
            val purchase = jsonObject.optJSONObject("data")?.let {
                PurchaseModel.parse(it)
            } ?: PurchaseModel()
            val purchases = jsonObject.optJSONArray("data").map { PurchaseModel.parse(it) }
            return PurchasesResponseModel(purchase, purchases)
        }
    }

    fun onDemandSubscription(value: String = IAPModel.ENTITLEMENT): PurchaseEntitlementModel? {
        purchases.forEach { item ->
            val entitlement = item.entitlements[value]
            if (entitlement?.isValid == true) {
                return entitlement
            }
        }
        return null
    }
}
