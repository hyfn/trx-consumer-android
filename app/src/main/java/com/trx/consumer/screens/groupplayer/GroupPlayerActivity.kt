package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trx.consumer.databinding.ActivityGroupPlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel.GROUP_PLAYER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupPlayerBinding

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doTrackPageView()

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
        analyticsManager.trackPageView(GROUP_PLAYER)
    }
}
