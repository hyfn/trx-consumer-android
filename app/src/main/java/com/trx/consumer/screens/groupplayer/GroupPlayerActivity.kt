package com.trx.consumer.screens.groupplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.trx.consumer.databinding.ActivityGroupPlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.checkLivePermission
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import dagger.hilt.android.AndroidEntryPoint
import fm.liveswitch.IAction1
import javax.inject.Inject

@AndroidEntryPoint
class GroupPlayerActivity : AppCompatActivity() {

    //region Objects
    private val viewModel: GroupPlayerViewModel by viewModels()
    private lateinit var viewBinding: ActivityGroupPlayerBinding

    @Inject
    lateinit var handler: GroupPlayerHandler

    private var localMediaStarted: Boolean = false

    val container
        get() = viewBinding.fmGroupPlayerContainer

    //endregion

    //region Initializers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGroupPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        bind()
    }

    private fun bind() {
        val workout = NavigationManager.shared.params(intent) as? WorkoutModel
        handler.groupPlayerActivity = this

        viewBinding.apply {
            btnCamera.onChecked { isChecked -> viewModel.doTapCamera(isChecked) }
            btnClock.onChecked { isChecked -> viewModel.doTapClock(isChecked) }
            btnMic.onChecked { isChecked -> viewModel.doTapMic(isChecked) }
            btnCast.onChecked { isChecked -> viewModel.doTapCast(isChecked) }
            btnCamera.onChecked { isChecked -> viewModel.doTapCamera(isChecked) }
            btnClose.action { viewModel.doTapClose() }
        }

        viewModel.apply {
            model = workout
            eventLoadVideo.observe(this@GroupPlayerActivity, handleLoadVideo)
            eventLoadError.observe(this@GroupPlayerActivity, handleLoadError)
            eventTapCamera.observe(this@GroupPlayerActivity, handleTapCamera)
            eventTapMic.observe(this@GroupPlayerActivity, handleTapMic)
            eventTapClock.observe(this@GroupPlayerActivity, handleTapClock)
            eventTapCast.observe(this@GroupPlayerActivity, handleTapCast)
            eventTapClose.observe(this@GroupPlayerActivity, handleTapClose)

            doTrackPageView()
            doLoadVideo()
        }
    }

    //endregion

    //region Handlers 

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadView: ${model.identifier}")
        playVideo(model.live)
    }

    private val handleLoadError = Observer<String> { value ->
        LogManager.log("handleLoadError: $value")
    }

    private val handleTapCamera = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapCamera $isChecked ")
    }

    private val handleTapMic = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapMicrophone $isChecked ")
    }

    private val handleTapClock = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapClock $isChecked ")
    }

    private val handleTapCast = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapShare $isChecked ")
    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        stopVideo()
    }

    //endregion

    //region Helper Functions

    private fun playVideo(value: LiveResponseModel) {
        if (!localMediaStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requiredPermissions: MutableList<String> = ArrayList(3)
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
                    handler.start(value)
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) || shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        ) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                    ) {
                        LogManager.log("Error Alert Implementation")
                        //  TODO: Error Alert implementation
                        // Toast.makeText(
                        //     this,
                        //     "Access to camera, microphone, storage, and phone call state is required",
                        //     Toast.LENGTH_SHORT
                        // ).show()
                    }
                    requestPermissions(requiredPermissions.toTypedArray(), 1)
                }
            } else {
                handler.start(value)
            }
        }

        localMediaStarted = true
    }

    private fun stopVideo() {
        if (localMediaStarted) {
            handler.leaveAsync()?.then {
                handler.cleanup().then {
                    finish()
                }?.fail(
                    IAction1 { e ->
                        LogManager.log("Could not stop local media: ${e.message}")
                    }
                )
            }?.fail(
                IAction1 { e ->
                    LogManager.log("Could not leave conference: ${e.message}")
                }
            )
        } else {
            finish()
        }
        localMediaStarted = false
    }

    //endregion

    //region Activity Overrides

    override fun onBackPressed() {
        stopVideo()
        super.onBackPressed()
    }

    //endregion
}
