package com.trx.consumer.models.common

import org.json.JSONObject

class UserMembershipModel(
    val cancelAtPeriodEnd: Boolean = false,
    val currentPeriodEnd: Long = 0,
    val currentPeriodStart: Long = 0,
    val isActive: Boolean = false,
) {

    companion object {

        fun parse(jsonObject: JSONObject): UserMembershipModel {
            return UserMembershipModel(
                cancelAtPeriodEnd = jsonObject.optBoolean("cancel_at_period_end"),
                currentPeriodEnd = jsonObject.optLong("current_period_end"),
                currentPeriodStart = jsonObject.optLong("current_period_start"),
                isActive = jsonObject.optString("status") == "active"
            )
        }
    }
}
