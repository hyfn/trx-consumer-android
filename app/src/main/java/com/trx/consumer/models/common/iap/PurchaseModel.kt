package com.trx.consumer.models.common.iap

import org.json.JSONObject

class PurchaseModel(val entitlements: Map<String, PurchaseEntitlementModel> = emptyMap()) {

    companion object {

        fun parse(jsonObject: JSONObject): PurchaseModel {
            val entitlementsObject = jsonObject
                .optJSONObject("subscriber")
                ?.optJSONObject("entitlements")
            val entitlements = entitlementsObject?.keys()?.asSequence()?.associate { key ->
                val entitlement = entitlementsObject.getJSONObject(key)
                key to PurchaseEntitlementModel.parse(entitlement)
            } ?: emptyMap()
            return PurchaseModel(entitlements)
        }
    }
}
