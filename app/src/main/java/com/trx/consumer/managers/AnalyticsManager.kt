package com.trx.consumer.managers

import android.content.Context
import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AmplitudePropertyModel
import com.trx.consumer.models.common.AnalyticsEventModel
import org.json.JSONObject

class AnalyticsManager(private val context: Context, private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun trackAmplitude(model: AnalyticsEventModel, value: Any?) {
        when (model.amplitudePropertyType) {
            AmplitudePropertyModel.EVENT -> {
                val properties = hashMapOf<String, Any>().apply {
                    if (value is String) {
                        put(model.amplitudePropertyName, value)
                    }
                }
                amplitudeClient.logEvent(model.amplitudeEventName, JSONObject(properties.toMap()))
            }
            else -> {}
        }
    }
}
