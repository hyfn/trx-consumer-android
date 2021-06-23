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
import com.trx.consumer.models.common.AnalyticsPropertyModel
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
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.models.common.VideoModel
import org.json.JSONObject

class AnalyticsManager(private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackCancelSubscription(model: UserModel) {
        val properties = mapOf<String, Any>(
            SUBSCRIPTION_ID.propertyName to model.uid
            //  TODO: No class data in UserModel for these keys.
            // ATTENDED_LIVE_CLASS.propertyName to false
            // DAYS_ELAPSED_IN_TRIAL.propertyName to 3
            // DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE.propertyName to 3
        )

        amplitudeClient.logEvent(CANCEL_SUBSCRIPTION.eventName, JSONObject(properties))
    }

    fun trackFilterOnDemand(model: FilterOptionsModel) {
        val properties = mapOf<String, Any>(ON_DEMAND_FILTER.propertyName to model.value)

        amplitudeClient.logEvent(FILTER_ON_DEMAND.eventName, JSONObject(properties))
    }

    fun trackPageView(value: Any?) {
        val properties = value?.let { mapOf(PAGE_TITLE.propertyName to value) } ?: mapOf()

        amplitudeClient.logEvent(PAGE_VIEW.eventName, JSONObject(properties))
    }

    fun trackSignIn(socialNetwork: String = "") {
        val properties = mapOf<String, Any>(
            SOCIAL_NETWORK.propertyName to socialNetwork,
            PLATFORM.propertyName to "Android"
        )

        amplitudeClient.logEvent(SIGN_IN.eventName, JSONObject(properties))
    }

    fun trackSignUp(userModel: UserModel?, regType: String) {
        val properties = mapOf<String, Any>(
            //  TODO: No data for these keys.
            // REGISTRATION_TYPE.propertyName to ""
            AnalyticsPropertyModel.REGISTRATION_TYPE.propertyName to regType
        )

        amplitudeClient.logEvent(SIGN_UP.eventName, JSONObject(properties))
    }

    fun trackVideoComplete100(model: VideoModel) {
        val properties = mapOf<String, Any>(
            DURATION.propertyName to model.duration,
            TRAINER_ID.propertyName to model.trainer.key,
            TRAINER_NAME.propertyName to model.trainer.fullName,
            VIDEO_ID.propertyName to model.id,
            VIDEO_NAME.propertyName to model.name

            // TODO: - Complete
            // PROGRAM_ID to video.id
            // COLLECTION_ID to video.id
        )

        amplitudeClient.logEvent(VIDEO_COMPLETE_100.eventName, JSONObject(properties))
    }

    fun trackVideoComplete25(model: VideoModel) {
        val properties = mapOf<String, Any>(
            DURATION.propertyName to model.duration,
            TRAINER_ID.propertyName to model.trainer.key,
            TRAINER_NAME.propertyName to model.trainer.fullName,
            VIDEO_ID.propertyName to model.id,
            VIDEO_NAME.propertyName to model.name

            // TODO: - Complete
            // PROGRAM_ID to video.id
            // COLLECTION_ID to video.id
        )

        amplitudeClient.logEvent(VIDEO_COMPLETE_25.eventName, JSONObject(properties))
    }

    fun trackViewVideo(model: VideoModel) {
        val properties = mapOf<String, Any>(
            TRAINER_ID.propertyName to model.trainer.key,
            TRAINER_NAME.propertyName to model.trainer.fullName,
            VIDEO_ID.propertyName to model.id,
            VIDEO_NAME.propertyName to model.name

            // TODO: Complete
            // PROGRAM_ID to video.id
            // COLLECTION_ID to video.id
        )

        amplitudeClient.logEvent(VIEW_VIDEO.eventName, JSONObject(properties))
    }

    fun trackViewVideoDetail(model: VideoModel) {
        val properties = mapOf<String, Any>(
            // TODO: No data for this key.
            // PAGE.propertyName to 1
            VIDEO_ID.propertyName to model.id
        )

        amplitudeClient.logEvent(VIEW_VIDEO_DETAIL.eventName, JSONObject(properties))
    }
}
