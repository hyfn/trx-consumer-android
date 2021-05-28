package com.trx.consumer.screens.settings.option

import com.trx.consumer.models.common.SettingsModel

interface SettingsOptionListener {
    fun doTapSettings(model: SettingsModel)
}
