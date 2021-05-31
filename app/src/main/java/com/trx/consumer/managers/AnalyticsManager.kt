package com.trx.consumer.managers

import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.models.common.AnalyticsPropertyModel

class AnalyticsManager(private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackAmplitude(
        event: AnalyticsEventModel,
        property: AnalyticsPropertyModel,
        value: Any?
    ) {
        when (property.propertyType) {
            "EVENT" -> {
                amplitudeClient.logEvent(
                    event.eventName,
                    event.getAmplitudePropertiesJSON(value)
                )
            }
            else -> {}
        }
    }
}
