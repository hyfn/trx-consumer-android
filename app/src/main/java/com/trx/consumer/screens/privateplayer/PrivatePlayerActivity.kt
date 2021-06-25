package com.trx.consumer.screens.privateplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trx.consumer.databinding.ActivityPrivatePlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager

class PrivatePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivatePlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        LogManager.log("handleTapCamera $isChecked")
    }

    private fun handleTapClock(isChecked: Boolean) {
        LogManager.log("handleTapClock $isChecked")
    }

    private fun handleTapMicrophone(isChecked: Boolean) {
        LogManager.log("handleTapMicrophone $isChecked")
    }

    private fun handleTapShare(isChecked: Boolean) {
        LogManager.log("handleTapShare $isChecked")
    }

    private fun handleTapEnd() {
        finish()
    }
}
