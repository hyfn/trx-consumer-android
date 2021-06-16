package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trx.consumer.databinding.ActivityGroupPlayerBinding
import com.trx.consumer.extensions.action

class GroupPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCamera.setOnCheckedChangeListener{ _ ,isChecked -> handleTapCamera(isChecked)}
            btnClock.setOnCheckedChangeListener{ _ ,isChecked -> handleTapClock(isChecked)}
            btnMicrophone.setOnCheckedChangeListener{ _ ,isChecked -> handleTapMicrophone(isChecked)}
            btnShare.setOnCheckedChangeListener{ _ ,isChecked -> handleTapShare(isChecked)}
            btnEnd.action { handleTapEnd() }
        }
    }

    private fun handleTapCamera(isChecked: Boolean) {
        //TODO: on camera actions
    }

    private fun handleTapClock(isChecked: Boolean) {
        //TODO: on clock actions
    }

    private fun handleTapMicrophone(isChecked: Boolean) {
        //TODO: on microphone actions
    }

    private fun handleTapShare(isChecked: Boolean) {
        //TODO: on share actions
    }

    private fun handleTapEnd() {
       finish()
    }
}
