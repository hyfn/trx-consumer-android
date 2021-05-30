package com.trx.consumer.managers

import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AnalyticsEventModel

class AnalyticsManager(private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackAmplitude(model: AnalyticsEventModel, value: Any?) {
        // when (model.amplitudePropertyType) {
        //     AmplitudePropertyModel.EVENT -> {
        //         val properties = hashMapOf<String, Any>().apply {
        //             if (value is String) {
        //                 put(model.amplitudePropertyName, value)
        //             }
        //         }
        //         amplitudeClient.logEvent(model.amplitudeEventName, JSONObject(properties.toMap()))
        //     }
        //     else -> {}
        // }
    }
}
