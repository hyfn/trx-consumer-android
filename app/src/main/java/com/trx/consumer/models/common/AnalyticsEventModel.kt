package com.trx.consumer.models.common

import com.trx.consumer.models.common.AmplitudePropertyModel.ATTENDED_LIVE_CLASS
import com.trx.consumer.models.common.AmplitudePropertyModel.COLLECTION_ID
import com.trx.consumer.models.common.AmplitudePropertyModel.DAYS_ELAPSED_IN_TRIAL
import com.trx.consumer.models.common.AmplitudePropertyModel.DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE
import com.trx.consumer.models.common.AmplitudePropertyModel.DURATION
import com.trx.consumer.models.common.AmplitudePropertyModel.OFFER
import com.trx.consumer.models.common.AmplitudePropertyModel.ON_DEMAND_FILTER
import com.trx.consumer.models.common.AmplitudePropertyModel.PAGE
import com.trx.consumer.models.common.AmplitudePropertyModel.PAGE_TITLE
import com.trx.consumer.models.common.AmplitudePropertyModel.PLATFORM
import com.trx.consumer.models.common.AmplitudePropertyModel.PROGRAM_ID
import com.trx.consumer.models.common.AmplitudePropertyModel.REGISTRATION_TYPE
import com.trx.consumer.models.common.AmplitudePropertyModel.SOCIAL_NETWORK
import com.trx.consumer.models.common.AmplitudePropertyModel.SUBSCRIPTION_ID
import com.trx.consumer.models.common.AmplitudePropertyModel.SUBSCRIPTION_PRICE
import com.trx.consumer.models.common.AmplitudePropertyModel.TRAINER_ID
import com.trx.consumer.models.common.AmplitudePropertyModel.TRAINER_NAME
import com.trx.consumer.models.common.AmplitudePropertyModel.VIDEO_ID
import com.trx.consumer.models.common.AmplitudePropertyModel.VIDEO_NAME

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

    val amplitudeEventName: String
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

    val amplitudeEventCategory: String
        get() {
            return when (this) {
                CANCEL_SUBSCRIPTION -> "Billing"
                FILTER_ON_DEMAND -> "On Demand"
                PAGE_VIEW -> "All"
                PURCHASE_SUBSCRIPTION -> "Billing"
                SIGN_IN -> "Sign In"
                SIGN_UP -> "Registration"
                VIDEO_COMPLETE_100 -> "On Demand"
                VIDEO_COMPLETE_25 -> "On Demand"
                VIEW_VIDEO -> "On Demand"
                VIEW_VIDEO_DETAIL -> "On Demand"
            }
        }

    val amplitudeProperties: List<AmplitudePropertyModel>
        get() {
            return when (this) {
                CANCEL_SUBSCRIPTION -> listOf(
                    ATTENDED_LIVE_CLASS,
                    DAYS_ELAPSED_IN_TRIAL,
                    DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE,
                    SUBSCRIPTION_ID
                )
                FILTER_ON_DEMAND -> listOf(ON_DEMAND_FILTER)
                PAGE_VIEW -> listOf(PAGE_TITLE)
                PURCHASE_SUBSCRIPTION -> listOf(SUBSCRIPTION_ID, SUBSCRIPTION_PRICE)
                SIGN_IN -> listOf(SOCIAL_NETWORK, PLATFORM)
                SIGN_UP -> listOf(REGISTRATION_TYPE, OFFER)
                VIDEO_COMPLETE_100 -> listOf(
                    COLLECTION_ID,
                    DURATION,
                    PROGRAM_ID,
                    TRAINER_ID,
                    TRAINER_NAME,
                    VIDEO_ID,
                    VIDEO_NAME
                )
                VIDEO_COMPLETE_25 -> listOf(
                    COLLECTION_ID,
                    DURATION,
                    PROGRAM_ID,
                    TRAINER_ID,
                    TRAINER_NAME,
                    VIDEO_ID,
                    VIDEO_NAME
                )
                VIEW_VIDEO -> listOf(
                    COLLECTION_ID,
                    PROGRAM_ID,
                    TRAINER_ID,
                    TRAINER_NAME,
                    VIDEO_ID,
                    VIDEO_NAME
                )
                VIEW_VIDEO_DETAIL -> listOf(PAGE, VIDEO_ID)
            }
        }
}
