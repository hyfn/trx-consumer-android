package com.trx.consumer.screens.privateplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trx.consumer.R
import com.trx.consumer.common.CommonStateButton
import com.trx.consumer.databinding.ActivityPrivatePlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel.PRIVATE_PLAYER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrivatePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivatePlayerBinding

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCamera.action { handleTapCamera() }
            btnClock.action {  handleTapClock() }
            btnMicrophone.action {  handleTapMicrophone() }
            btnShare.action {  handleTapShare() }
            btnCamera.action { handleTapCamera() }
            btnEnd.action { handleTapEnd() }
        }
    }

    private fun handleTapCamera() {
        LogManager.log("handleTapCamera")
    }

    private fun handleTapClock() {
        LogManager.log("handleTapClock")
    }

    private fun handleTapMicrophone() {
        LogManager.log("handleTapMicrophone")
    }

    private fun handleTapShare() {
        LogManager.log("handleTapShare")
    }

    private fun handleTapEnd() {
        finish()
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(PRIVATE_PLAYER)
    }
}
