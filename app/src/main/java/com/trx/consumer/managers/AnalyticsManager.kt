package com.trx.consumer.managers

import android.content.Context
import com.amplitude.api.AmplitudeClient
import com.trx.consumer.models.common.AnalyticsEventModel
import io.branch.referral.Branch
import io.branch.referral.util.BranchEvent


class AnalyticsManager(private val context: Context, private val configManager: ConfigManager) {

    private val amplitudeClient: AmplitudeClient
        get() = configManager.amplitudeClient

    fun setUserId(id: String) {
        amplitudeClient.userId = id
        Branch.getInstance().setIdentity(id)
    }

    fun track(model: AnalyticsEventModel) {
        trackAmplitude(model)
        trackBranch(model)
    }

    private fun trackAmplitude(model: AnalyticsEventModel) {
        amplitudeClient.logEvent(model.amplitudeEventName)
    }

    private fun trackBranch(model: AnalyticsEventModel) {
        BranchEvent(model.branchEvent.getName()).logEvent(context)
    }
}
