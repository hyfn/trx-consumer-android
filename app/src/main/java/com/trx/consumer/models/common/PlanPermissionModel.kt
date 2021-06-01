package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class PlanPermissionModel(
    val onDemand: Boolean = false,
    val bookLiveClassWithSub: Boolean = false,
    val bookGroupClassWithCredit: Boolean = false,
    val bookVptWithCredit: Boolean = false,
    val createTrainerServices: Boolean = false,
    val showPublicTrainerServices: Boolean = false,
    val storeTrainerSubscriberDiscounts: Boolean = false,
    val storeCustomerSubscriberDiscounts: Boolean = false,
    val entitleCoreInspire360: Boolean = false
) : Parcelable {

    companion object {

        fun parse(jsonObject: JSONObject): PlanPermissionModel {
            return PlanPermissionModel()
        }
    }
}
