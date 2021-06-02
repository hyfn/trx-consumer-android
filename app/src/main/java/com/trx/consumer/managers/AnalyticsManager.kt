package com.trx.consumer.managers

import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AnalyticsEventModel.CANCEL_SUBSCRIPTION
import com.trx.consumer.models.common.AnalyticsEventModel.FILTER_ON_DEMAND
import com.trx.consumer.models.common.AnalyticsEventModel.PAGE_VIEW
import com.trx.consumer.models.common.AnalyticsEventModel.PURCHASE_SUBSCRIPTION
import com.trx.consumer.models.common.AnalyticsEventModel.SIGN_IN
import com.trx.consumer.models.common.AnalyticsEventModel.SIGN_UP
import com.trx.consumer.models.common.AnalyticsEventModel.VIDEO_COMPLETE_100
import com.trx.consumer.models.common.AnalyticsEventModel.VIDEO_COMPLETE_25
import com.trx.consumer.models.common.AnalyticsEventModel.VIEW_VIDEO
import com.trx.consumer.models.common.AnalyticsEventModel.VIEW_VIDEO_DETAIL
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
import com.trx.consumer.models.common.FilterOptionsModel
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.models.common.VideoModel
import org.json.JSONObject

class AnalyticsManager(private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackCancelSubscription(user: UserModel) {
        val value = mapOf<String, Any>(
            SUBSCRIPTION_ID.propertyName to user.uid
            //  TODO: No class data in UserModel for these keys.
            // ATTENDED_LIVE_CLASS.propertyName to false
            // DAYS_ELAPSED_IN_TRIAL.propertyName to 3
            // DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE.propertyName to 3
        )

        amplitudeClient.logEvent(
            CANCEL_SUBSCRIPTION.eventName,
            JSONObject((value))
        )
    }

    fun trackFilterOnDemand(filterOptions: FilterOptionsModel) {
        val value = mapOf<String, Any>(
            ON_DEMAND_FILTER.propertyName to filterOptions.identifier
        )

        amplitudeClient.logEvent(
            FILTER_ON_DEMAND.eventName,
            JSONObject((value))
        )
    }

    fun trackPageView(pageTitle: String) {
        val value = mapOf<String, Any>(
            //  TODO: No data for this key.
            PAGE_TITLE.propertyName to pageTitle
        )

        amplitudeClient.logEvent(
            PAGE_VIEW.eventName,
            JSONObject((value))
        )
    }

    fun trackPurchaseSubscription(subscription: SubscriptionModel) {
        val value = mapOf<String, Any>(
            SUBSCRIPTION_ID.propertyName to subscription.iapPackage.identifier,
            SUBSCRIPTION_PRICE.propertyName to subscription.cost
        )

        amplitudeClient.logEvent(
            PURCHASE_SUBSCRIPTION.eventName,
            JSONObject((value))
        )
    }

    fun trackSignIn(socialNetwork: String = "") {
        val value = mapOf<String, Any>(
            //  TODO: Incomplete, does not account for SOCIAL_NETWORK
            SOCIAL_NETWORK.propertyName to socialNetwork,
            PLATFORM.propertyName to "Android"
        )

        amplitudeClient.logEvent(
            SIGN_IN.eventName,
            JSONObject((value))
        )
    }

    fun trackSignUp(user: UserModel) {
        val value = mapOf<String, Any>(
            //  TODO: No data for these keys.
            // REGISTRATION_TYPE.propertyName to ""
            // OFFER.propertyName to ""
        )

        amplitudeClient.logEvent(
            SIGN_UP.eventName,
            JSONObject((value))
        )
    }

    fun trackVideoComplete100(video: VideoModel) {
        val value = mapOf<String, Any>(
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

        amplitudeClient.logEvent(
            VIDEO_COMPLETE_100.eventName,
            JSONObject((value))
        )
    }

    fun trackVideoComplete25(video: VideoModel) {
        val value = mapOf<String, Any>(
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

        amplitudeClient.logEvent(
            VIDEO_COMPLETE_25.eventName,
            JSONObject((value))
        )
    }

    fun trackViewVideo(video: VideoModel) {
        val value = mapOf<String, Any>(
            //  TODO: No data for this key.
            // COLLECTION_ID to video.id
            //  TODO: No data for this key.
            // PROGRAM_ID to video.id
            TRAINER_ID.propertyName to video.trainer.key,
            TRAINER_NAME.propertyName to video.trainer.fullName,
            VIDEO_ID.propertyName to video.id,
            VIDEO_NAME.propertyName to video.name
        )

        amplitudeClient.logEvent(
            VIEW_VIDEO.eventName,
            JSONObject((value))
        )
    }

    fun trackViewVideoDetail(video: VideoModel) {
        val value = mapOf<String, Any>(
            //  TODO: No data for this key.
            // PAGE.propertyName to 1
            VIDEO_ID.propertyName to video.id
        )

        amplitudeClient.logEvent(
            VIEW_VIDEO_DETAIL.eventName,
            JSONObject((value))
        )
    }
}
