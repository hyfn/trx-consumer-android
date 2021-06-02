package com.trx.consumer.managers

import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AnalyticsEventModel
import org.json.JSONObject

class AnalyticsManager(private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackAmplitude(
        event: AnalyticsEventModel,
        value: Any? = null
    ) {
        amplitudeClient.logEvent(
            event.eventName,
            JSONObject(event.trackAnalyticEvent(value))
        )
    }
}
