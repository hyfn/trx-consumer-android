package com.trx.consumer.models.common

enum class AnalyticsEventModel {

    CANCEL_SUBSCRIPTION,
    FILTER_ON_DEMAND,
    PAGE_VIEW,
    PURCHASE_SUBSCRIPTION,
    SIGN_IN,
    SIGN_UP,
    VIDEO_COMPLETE_100,
    VIDEO_COMPLETE_25,
    VIEW_VIDEO,
    VIEW_VIDEO_DETAIL;

    val eventName: String
        get() {
            return when (this) {
                CANCEL_SUBSCRIPTION -> "Cancel Subscription"
                FILTER_ON_DEMAND -> "Filter On Demand"
                PAGE_VIEW -> "Page View"
                PURCHASE_SUBSCRIPTION -> "Purchase Subscription"
                SIGN_IN -> "Sign In"
                SIGN_UP -> "Sign Up"
                VIDEO_COMPLETE_100 -> "Video Complete 100"
                VIDEO_COMPLETE_25 -> "Video Complete 25"
                VIEW_VIDEO -> "View Video"
                VIEW_VIDEO_DETAIL -> "View Video Detail"
            }
        }
}
