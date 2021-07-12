package com.trx.consumer.screens.privateplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.trx.consumer.databinding.ActivityPrivatePlayerBinding
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
class PrivatePlayerActivity : AppCompatActivity() {

    //region Objects
    private val viewModel: PrivatePlayerViewModel by viewModels()
    private lateinit var viewBinding: ActivityPrivatePlayerBinding

    @Inject
    lateinit var handler: PrivatePlayerHandler

    var container: RelativeLayout? = null

    //endregion

    //region Initializers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPrivatePlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        bind()
    }

    private fun bind() {
        val workout = NavigationManager.shared.params(intent) as? WorkoutModel
        container = viewBinding.fmPrivatePlayerContainer
        handler.apply {
            privatePlayerActivity = this@PrivatePlayerActivity
            listener = viewModel
            if (handlerScope == null) handlerScope = viewModel.viewModelScope
        }

        viewBinding.apply {
            btnCamera.action { viewModel.doTapCamera() }
            btnClock.action { viewModel.doTapClock() }
            btnMic.action { viewModel.doTapMic() }
            btnCast.action { viewModel.doTapCast() }
            btnClose.action { viewModel.doTapClose() }
        }

        viewModel.apply {
            model = workout
            eventLoadVideo.observe(this@PrivatePlayerActivity, handleLoadVideo)
            eventLoadError.observe(this@PrivatePlayerActivity, handleLoadError)
            eventTapCamera.observe(this@PrivatePlayerActivity, handleTapCamera)
            eventTapMic.observe(this@PrivatePlayerActivity, handleTapMic)
            eventTapClock.observe(this@PrivatePlayerActivity, handleTapClock)
            eventTapCast.observe(this@PrivatePlayerActivity, handleTapCast)
            eventTapClose.observe(this@PrivatePlayerActivity, handleTapClose)

            doTrackPageView()
            doLoadVideo()
        }
    }

    //endregion

    //region Activity Lifecycle

    override fun onDestroy() {
        super.onDestroy()
        handler.leaveAsync()?.waitForResult()
        container = null
        viewModel.localMediaStarted = true
    }

    //endregion

    //region Activity Helper Overrides

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            // stream api not used here bc not supported under api 24
            var permissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false
                }
            }
            if (permissionsGranted) {
                viewModel.localMediaStarted = false
                viewModel.doLoadVideo()
            } else {
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        LogManager.log("permission to " + permissions[i] + " not granted")
                    }
                }
                //  TODO: Error Alert fragment implementation
            }
        } else {
            //  TODO: Error Alert fragment implementation
            LogManager.log("Unknown permission requested")
        }
    }

    override fun onBackPressed() {
        stopVideo()
        super.onBackPressed()
    }

    //endregion

    //region Handlers

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadView: ${model.identifier}")
        playVideo(model.live)
    }

    private val handleLoadError = Observer<String> { error ->
        LogManager.log("handleLoadError: $error")
        //  TODO: Implement with another ticket.
        // val model = ErrorAlertModel.error(error)
        // NavigationManager.shared.present(this, R.id.error_fragment, model)
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

    //  TODO: Needs to be reworked when permissions screens brought in.
    private fun playVideo(value: LiveResponseModel) {
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
                }
                requestPermissions(requiredPermissions.toTypedArray(), 1)
            }
        } else {
            handler.start(value)
        }
    }

    private fun stopVideo() {
        if (viewModel.localMediaStarted) {
            handler.leaveAsync()?.then {
                handler.cleanup().then {
                    container = null
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
    }

    //endregion
}
