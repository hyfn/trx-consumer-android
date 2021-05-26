package com.trx.consumer.models.common

import io.branch.referral.util.BRANCH_STANDARD_EVENT

enum class AnalyticsEventModel {

    AUTH_SUCCESS,
    BOOKING_SUCCESS,
    PACKAGES_TAP_BUY,
    PURCHASE_SUCCESS;

    val amplitudeEventName: String
        get() {
            return when (this) {
                AUTH_SUCCESS -> "authentication_success"
                BOOKING_SUCCESS -> "booking_success"
                PACKAGES_TAP_BUY -> "packages_tap purchase"
                PURCHASE_SUCCESS -> "purchase_success"
            }
        }

    val branchEvent: BRANCH_STANDARD_EVENT
        get() {
            return when (this) {
                AUTH_SUCCESS -> BRANCH_STANDARD_EVENT.COMPLETE_REGISTRATION
                BOOKING_SUCCESS -> BRANCH_STANDARD_EVENT.ADD_TO_CART
                PACKAGES_TAP_BUY -> BRANCH_STANDARD_EVENT.INITIATE_PURCHASE
                PURCHASE_SUCCESS -> BRANCH_STANDARD_EVENT.PURCHASE
            }
        }
}
