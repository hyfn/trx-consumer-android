package com.trx.consumer.screens.privateplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.databinding.ActivityPrivatePlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.checkLivePermission
import com.trx.consumer.frozenmountain.RemoteMedia
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AnalyticsPageModel.PRIVATE_PLAYER
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import dagger.hilt.android.AndroidEntryPoint
import fm.liveswitch.IAction0
import fm.liveswitch.Promise
import javax.inject.Inject

@AndroidEntryPoint
class PrivatePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivatePlayerBinding
    private var localMediaStarted: Boolean = false

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var privatePlayerHandler: PrivatePlayerHandler

    lateinit var trainerContainer: FrameLayout
    lateinit var localMediaContainer: FrameLayout

    private val viewModel: PrivatePlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trainerContainer = findViewById(R.id.privatePlayerTrainerView)
        localMediaContainer = findViewById(R.id.privatePlayerLocalMediaView)

        binding.apply {
            btnCamera.onChecked { isChecked -> handleTapCamera(isChecked) }
            btnClock.onChecked { isChecked -> handleTapClock(isChecked) }
            btnMicrophone.onChecked { isChecked -> handleTapMicrophone(isChecked) }
            btnShare.onChecked { isChecked -> handleTapShare(isChecked) }
            btnCamera.onChecked { isChecked -> handleTapCamera(isChecked) }
            btnEnd.action { handleTapEnd() }
        }

        if(savedInstanceState != null) {
            localMediaStarted = savedInstanceState.getBoolean("localMediaStarted")
            this.runOnUiThread {
                var localmedia = privatePlayerHandler.getLocalMediaView()
                localmedia?.let { lm ->
                    lm.start().waitForResult()
                    if(lm.viewSink != null) {
                        lm.viewSink.view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    localMediaContainer.addView(lm.view)
                }
                var trainerMedia = privatePlayerHandler.getTrainerMediaView()
                trainerMedia?.let { tm ->
                    trainerContainer.addView(tm.view)
                }
            }
        }

        bind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var localmedia = privatePlayerHandler.getLocalMediaView()
        localmedia?.let { lm ->
            lm.stop().waitForResult()
        }
        trainerContainer.removeAllViewsInLayout()
        localMediaContainer.removeAllViewsInLayout()
        outState.putBoolean("localMediaStarted", localMediaStarted)
    }

    private fun bind() {
        privatePlayerHandler.apply {
            eventTrainerLoaded.observe(this@PrivatePlayerActivity, addTrainerToLayout)
        }

        val liveResponseModel = LiveResponseModel.test()
        playTRXlive(liveResponseModel)
    }

    private fun playTRXlive(value: LiveResponseModel) {
        if (!localMediaStarted) {
            val promise = Promise<Any>()

            val startFn = IAction0 {
                privatePlayerHandler.startTRXLocalMedia().then({ resultStart ->
                    this.runOnUiThread {
                        var localMedia = privatePlayerHandler.getLocalMediaView()
                        localMedia?.let { lm ->
                            localMediaContainer.addView(lm.view)
                        }
                    }
                    privatePlayerHandler.joinAsync()?.then({ resultJoin ->
                        promise.resolve(null)
                    }) { ex ->
                        promise.reject(ex)
                    }
                }) { ex ->
                    promise.reject(null)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requiredPermissions: MutableList<String> = java.util.ArrayList(3)
                if (checkLivePermission(Manifest.permission.RECORD_AUDIO)) {
                    requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
                }
                if (checkLivePermission(Manifest.permission.CAMERA)) {
                    requiredPermissions.add(Manifest.permission.CAMERA)
                }
                if (checkLivePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (checkLivePermission(Manifest.permission.READ_PHONE_STATE)) {
                    requiredPermissions.add(Manifest.permission.READ_PHONE_STATE)
                }
                if (requiredPermissions.size == 0) {
                    startFn.invoke()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) || shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        ) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                    ) {
                        Toast.makeText(
                            this,
                            "Access to camera, microphone, storage, and phone call state is required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    requestPermissions(requiredPermissions.toTypedArray(), 1)
                }
            } else {
                startFn.invoke()
            }
        }

        localMediaStarted = true
    }

    private val addTrainerToLayout = Observer<Boolean> { trainerLoaded ->
        LogManager.log("trainerLoaded")
        this.runOnUiThread {
            var trainerMedia = this.privatePlayerHandler.getTrainerMediaView()
            if (trainerLoaded) {
                trainerMedia?.let { tm ->
                    tm.view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
                    trainerContainer.addView(tm.view)
                }
            } else {
                trainerContainer.removeAllViewsInLayout()
            }
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

    fun doTrackPageView() {
        analyticsManager.trackPageView(PRIVATE_PLAYER)
    }
}
