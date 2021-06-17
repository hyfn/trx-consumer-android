package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class EntitlementsModel(
    val onDemand: Boolean = false,
    val bookLiveClassWithSub: Boolean = false,
    val bookLiveClassWithCredit: Boolean = false,
    val bookGroupClassWithCredit: Boolean = false,
    val bookVptWithCredit: Boolean = false,
    val createTrainerServices: Boolean = false,
    val showPublicTrainerServices: Boolean = false,
    val storeTrainerSubscriberDiscounts: Boolean = false,
    val storeCustomerSubscriberDiscounts: Boolean = false,
    val entitleCoreInspire360: Boolean = false
) : Parcelable {

    companion object {

        fun parse(jsonObject: JSONObject): EntitlementsModel {
            return EntitlementsModel(
                bookGroupClassWithCredit = jsonObject.optBoolean("bookGroupClassWithCredit"),
                bookLiveClassWithCredit = jsonObject.optBoolean("bookLiveClassWithSub"),
                bookLiveClassWithSub = jsonObject.optBoolean("bookLiveClassWithSub"),
                bookVptWithCredit = jsonObject.optBoolean("bookVptWithCredit"),
                createTrainerServices = jsonObject.optBoolean("createTrainerServices"),
                entitleCoreInspire360 = jsonObject.optBoolean("entitleCoreInspire360"),
                onDemand = jsonObject.optBoolean("onDemand"),
                showPublicTrainerServices = jsonObject.optBoolean("showPublicTrainerServices"),
                storeCustomerSubscriberDiscounts = jsonObject.optBoolean("storeCustomerSubscriberDiscounts"),
                storeTrainerSubscriberDiscounts = jsonObject.optBoolean("storeTrainerSubscriberDiscounts")
            )
        }
    }
}