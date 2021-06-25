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
            btnCamera.onChecked { isChecked -> handleTapCamera(isChecked) }
            btnClock.onChecked { isChecked -> handleTapClock(isChecked) }
            btnMicrophone.onChecked { isChecked -> handleTapMicrophone(isChecked) }
            btnShare.onChecked { isChecked -> handleTapShare(isChecked) }
            btnCamera.onChecked { isChecked -> handleTapCamera(isChecked) }
            btnEnd.action { handleTapEnd() }
        }
    }

    private fun handleTapCamera(isChecked: Boolean) {
        LogManager.log("handleTapCamera $isChecked ")
    }

    private fun handleTapClock(isChecked: Boolean) {
        LogManager.log("handleTapClock $isChecked ")
    }

    private fun handleTapMicrophone(isChecked: Boolean) {
        LogManager.log("handleTapMicrophone $isChecked ")
    }

    private fun handleTapShare(isChecked: Boolean) {
        LogManager.log("handleTapShare $isChecked ")
    }

    private fun handleTapEnd() {
        finish()
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(GROUP_PLAYER)
    }
}
