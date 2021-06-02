package com.trx.consumer.models.common

import com.trx.consumer.models.common.AnalyticsPropertyModel.DURATION
import com.trx.consumer.models.common.AnalyticsPropertyModel.ON_DEMAND_FILTER
import com.trx.consumer.models.common.AnalyticsPropertyModel.PAGE_TITLE
import com.trx.consumer.models.common.AnalyticsPropertyModel.PLATFORM
import com.trx.consumer.models.common.AnalyticsPropertyModel.SOCIAL_NETWORK
import com.trx.consumer.models.common.AnalyticsPropertyModel.SUBSCRIPTION_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.SUBSCRIPTION_PRICE
import com.trx.consumer.models.common.AnalyticsPropertyModel.TRAINER_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.TRAINER_NAME
import com.trx.consumer.models.common.AnalyticsPropertyModel.VIDEO_ID
import com.trx.consumer.models.common.AnalyticsPropertyModel.VIDEO_NAME

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

    fun trackAnalyticEvent(value: Any?): Map<String, Any> =
        when (this) {
            CANCEL_SUBSCRIPTION -> trackCancelSubscription(value as UserModel)
            FILTER_ON_DEMAND -> trackFilterOnDemand(value as FilterOptionsModel)
            PAGE_VIEW -> trackPageView(value as String)
            PURCHASE_SUBSCRIPTION -> trackPurchaseSubscription(value as SubscriptionModel)
            SIGN_IN -> value?.let { trackSignin(it as String) } ?: trackSignin()
            SIGN_UP -> trackSignUp(value as UserModel)
            VIDEO_COMPLETE_100 -> trackVideoComplete100(value as VideoModel)
            VIDEO_COMPLETE_25 -> trackVideoComplete25(value as VideoModel)
            VIEW_VIDEO -> trackViewVideo(value as VideoModel)
            VIEW_VIDEO_DETAIL -> trackViewVideoDetail(value as VideoModel)
        }

    private fun trackCancelSubscription(user: UserModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            SUBSCRIPTION_ID.propertyName to user.uid
            //  TODO: No class data in UserModel for these keys.
            // ATTENDED_LIVE_CLASS.propertyName to false
            // DAYS_ELAPSED_IN_TRIAL.propertyName to 3
            // DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE.propertyName to 3
        }.toMap()

    private fun trackFilterOnDemand(filterOptions: FilterOptionsModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            ON_DEMAND_FILTER.propertyName to filterOptions.identifier
        }.toMap()

    private fun trackPageView(pageTitle: String): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: No data for this key.
            PAGE_TITLE.propertyName to pageTitle
        }.toMap()

    private fun trackPurchaseSubscription(subscription: SubscriptionModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            SUBSCRIPTION_ID to subscription.iapPackage.identifier
            SUBSCRIPTION_PRICE to subscription.cost
        }.toMap()

    private fun trackSignin(socialNetwork: String = ""): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: Incomplete, does not account for SOCIAL_NETWORK
            SOCIAL_NETWORK to socialNetwork
            PLATFORM to "Android"
        }.toMap()

    private fun trackSignUp(user: UserModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: No data for these keys.
            // REGISTRATION_TYPE.propertyName to ""
            // OFFER.propertyName to ""
        }.toMap()

    private fun trackVideoComplete100(video: VideoModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            DURATION to video.duration
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID to video.trainer.key
            TRAINER_NAME to video.trainer.fullName
            VIDEO_ID to video.id
            VIDEO_NAME to video.name
        }.toMap()

    private fun trackVideoComplete25(video: VideoModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            DURATION to video.duration
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID to video.trainer.key
            TRAINER_NAME to video.trainer.fullName
            VIDEO_ID to video.id
            VIDEO_NAME to video.name
        }.toMap()

    private fun trackViewVideo(video: VideoModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID to video.trainer.key
            TRAINER_NAME to video.trainer.fullName
            VIDEO_ID to video.id
            VIDEO_NAME to video.name
        }.toMap()

    private fun trackViewVideoDetail(video: VideoModel): Map<String, Any> =
        hashMapOf<String, Any>().apply {
            //  TODO: No data for this key.
            // PAGE to 1
            VIDEO_ID to video.id
        }.toMap()
}
