package com.trx.consumer.managers

import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AnalyticsEventModel

class AnalyticsManager(private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackAmplitude(
        event: AnalyticsEventModel,
        value: Any?
    ) {
        amplitudeClient.logEvent(
            event.eventName,
            event.getAmplitudePropertiesJSON(value)
        )
    }
}
