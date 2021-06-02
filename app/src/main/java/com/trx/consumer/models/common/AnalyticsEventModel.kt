package com.trx.consumer.models.common

import com.trx.consumer.models.common.AnalyticsPropertyModel.ATTENDED_LIVE_CLASS
import com.trx.consumer.models.common.AnalyticsPropertyModel.COLLECTION_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.DAYS_ELAPSED_IN_TRIAL
import com.trx.consumer.models.common.AnalyticsPropertyModel.DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE
import com.trx.consumer.models.common.AnalyticsPropertyModel.DURATION
import com.trx.consumer.models.common.AnalyticsPropertyModel.OFFER
import com.trx.consumer.models.common.AnalyticsPropertyModel.ON_DEMAND_FILTER
import com.trx.consumer.models.common.AnalyticsPropertyModel.PAGE
import com.trx.consumer.models.common.AnalyticsPropertyModel.PAGE_TITLE
import com.trx.consumer.models.common.AnalyticsPropertyModel.PLATFORM
import com.trx.consumer.models.common.AnalyticsPropertyModel.PROGRAM_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.REGISTRATION_TYPE
import com.trx.consumer.models.common.AnalyticsPropertyModel.SOCIAL_NETWORK
import com.trx.consumer.models.common.AnalyticsPropertyModel.SUBSCRIPTION_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.SUBSCRIPTION_PRICE
import com.trx.consumer.models.common.AnalyticsPropertyModel.TRAINER_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.TRAINER_NAME
import com.trx.consumer.models.common.AnalyticsPropertyModel.VIDEO_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.VIDEO_NAME
import org.json.JSONObject

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

    val amplitudeProperties: List<AnalyticsPropertyModel>
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
                PURCHASE_SUBSCRIPTION -> listOf(
                    SUBSCRIPTION_ID,
                    SUBSCRIPTION_PRICE
                )
                SIGN_IN -> listOf(
                    SOCIAL_NETWORK,
                    PLATFORM
                )
                SIGN_UP -> listOf(
                    REGISTRATION_TYPE,
                    OFFER
                )
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
                VIEW_VIDEO_DETAIL -> listOf(
                    PAGE,
                    VIDEO_ID
                )
            }
        }

    fun getAmplitudePropertiesJSON(value: Any?): JSONObject {
        val properties = when (this) {
            CANCEL_SUBSCRIPTION -> {
                (value as UserModel).let {
                    hashMapOf<String, Any>().apply {
                        ATTENDED_LIVE_CLASS.propertyName to ""
                        DAYS_ELAPSED_IN_TRIAL.propertyName to ""
                        DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE.propertyName to ""
                        SUBSCRIPTION_ID.propertyName to ""
                    }
                }
            }
            FILTER_ON_DEMAND -> {
                (value as FilterOptionsModel).let { filterOptions ->
                    hashMapOf<String, Any>().apply {
                        ON_DEMAND_FILTER.propertyName to filterOptions.identifier
                    }
                }
            }
            PAGE_VIEW -> {
                (value as String).let { pageTitle ->
                    hashMapOf<String, Any>().apply {
                        PAGE_TITLE.propertyName to pageTitle
                    }
                }
            }
            PURCHASE_SUBSCRIPTION -> {
                (value as SubscriptionModel).let { subscription ->
                    hashMapOf<String, Any>().apply {
                        SUBSCRIPTION_ID to subscription.iapPackage.identifier
                        SUBSCRIPTION_PRICE to subscription.cost
                    }
                }
            }
            SIGN_IN -> {
                //  TODO: Incomplete, does not account for SOCIAL_NETWORK
                hashMapOf<String, Any>().apply {
                    PLATFORM to "Android"
                }
            }
            SIGN_UP -> {
                (value as UserModel).let { user ->
                    hashMapOf<String, Any>().apply {
                        REGISTRATION_TYPE.propertyName to ""
                        OFFER.propertyName to ""
                    }
                }
            }
            VIDEO_COMPLETE_100 -> {
                (value as VideoModel).let { video ->
                    hashMapOf<String, Any>().apply {
                        //  TODO: Get correct value.
                        COLLECTION_ID to video.id
                        DURATION to video.duration
                        //  TODO: Get correct value.
                        PROGRAM_ID to video.id
                        TRAINER_ID to video.trainer.key
                        TRAINER_NAME to video.trainer.fullName
                        VIDEO_ID to video.id
                        VIDEO_NAME to video.name
                    }
                }
            }
            VIDEO_COMPLETE_25 -> {
                (value as VideoModel).let { video ->
                    hashMapOf<String, Any>().apply {
                        //  TODO: Get correct value.
                        COLLECTION_ID to video.id
                        DURATION to video.duration
                        //  TODO: Get correct value.
                        PROGRAM_ID to video.id
                        TRAINER_ID to video.trainer.key
                        TRAINER_NAME to video.trainer.fullName
                        VIDEO_ID to video.id
                        VIDEO_NAME to video.name
                    }
                }
            }
            VIEW_VIDEO -> {
                (value as VideoModel).let { video ->
                    hashMapOf<String, Any>().apply {
                        //  TODO: Get correct value.
                        COLLECTION_ID to video.id
                        //  TODO: Get correct value.
                        PROGRAM_ID to video.id
                        TRAINER_ID to video.trainer.key
                        TRAINER_NAME to video.trainer.fullName
                        VIDEO_ID to video.id
                        VIDEO_NAME to video.name
                    }
                }
            }
            VIEW_VIDEO_DETAIL -> {
                (value as VideoModel).let { video ->
                    hashMapOf<String, Any>().apply {
                        PAGE to 1
                        VIDEO_ID to video.id
                    }
                }
            }
        }

        return JSONObject(properties.toMap())
    }
}
