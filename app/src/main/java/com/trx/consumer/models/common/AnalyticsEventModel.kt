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

    fun trackAnalyticEvent(value: Any?): Map<String, Any> =
        when (this) {
            CANCEL_SUBSCRIPTION -> trackCancelSubscription(value as UserModel)
            FILTER_ON_DEMAND -> trackFilterOnDemand(value as FilterOptionsModel)
            PAGE_VIEW -> trackPageView(value as String)
            PURCHASE_SUBSCRIPTION -> trackPurchaseSubscription(value as SubscriptionModel)
            SIGN_IN -> value?.let { trackSignIn(it as String) } ?: trackSignIn()
            SIGN_UP -> trackSignUp(value as UserModel)
            VIDEO_COMPLETE_100 -> trackVideoComplete100(value as VideoModel)
            VIDEO_COMPLETE_25 -> trackVideoComplete25(value as VideoModel)
            VIEW_VIDEO -> trackViewVideo(value as VideoModel)
            VIEW_VIDEO_DETAIL -> trackViewVideoDetail(value as VideoModel)
        }

    private fun trackCancelSubscription(user: UserModel): Map<String, Any> =
        mapOf<String, Any>(
            SUBSCRIPTION_ID.propertyName to user.uid
            //  TODO: No class data in UserModel for these keys.
            // ATTENDED_LIVE_CLASS.propertyName to false
            // DAYS_ELAPSED_IN_TRIAL.propertyName to 3
            // DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE.propertyName to 3
        )

    private fun trackFilterOnDemand(filterOptions: FilterOptionsModel): Map<String, Any> =
        mapOf<String, Any>(
            ON_DEMAND_FILTER.propertyName to filterOptions.identifier
        )

    private fun trackPageView(pageTitle: String): Map<String, Any> =
        mapOf<String, Any>(
            //  TODO: No data for this key.
            PAGE_TITLE.propertyName to pageTitle
        )

    private fun trackPurchaseSubscription(subscription: SubscriptionModel): Map<String, Any> =
        mapOf<String, Any>(
            SUBSCRIPTION_ID.propertyName to subscription.iapPackage.identifier,
            SUBSCRIPTION_PRICE.propertyName to subscription.cost
        )

    private fun trackSignIn(socialNetwork: String = ""): Map<String, Any> =
        mapOf<String, Any>(
            //  TODO: Incomplete, does not account for SOCIAL_NETWORK
            SOCIAL_NETWORK.propertyName to socialNetwork,
            PLATFORM.propertyName to "Android"
        )

    private fun trackSignUp(user: UserModel): Map<String, Any> =
        mapOf(
            //  TODO: No data for these keys.
            // REGISTRATION_TYPE.propertyName to ""
            // OFFER.propertyName to ""
        )

    private fun trackVideoComplete100(video: VideoModel): Map<String, Any> =
        mapOf<String, Any>(
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            DURATION.propertyName to video.duration,
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID.propertyName to video.trainer.key,
            TRAINER_NAME.propertyName to video.trainer.fullName,
            VIDEO_ID.propertyName to video.id,
            VIDEO_NAME.propertyName to video.name
        )

    private fun trackVideoComplete25(video: VideoModel): Map<String, Any> =
        mapOf<String, Any>(
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            DURATION.propertyName to video.duration,
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID.propertyName to video.trainer.key,
            TRAINER_NAME.propertyName to video.trainer.fullName,
            VIDEO_ID.propertyName to video.id,
            VIDEO_NAME.propertyName to video.name
        )

    private fun trackViewVideo(video: VideoModel): Map<String, Any> =
        mapOf<String, Any>(
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID.propertyName to video.trainer.key,
            TRAINER_NAME.propertyName to video.trainer.fullName,
            VIDEO_ID.propertyName to video.id,
            VIDEO_NAME.propertyName to video.name
        )

    private fun trackViewVideoDetail(video: VideoModel): Map<String, Any> =
        mapOf<String, Any>(
            //  TODO: No data for this key.
            // PAGE.propertyName to 1
            VIDEO_ID.propertyName to video.id
        )
}
