package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.trx.consumer.databinding.ActivityGroupPlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.WorkoutModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupPlayerActivity : AppCompatActivity() {

    //region Objects
    private val viewModel: GroupPlayerViewModel by viewModels()
    private lateinit var viewBinding: ActivityGroupPlayerBinding

    @Inject
    lateinit var handler: GroupPlayerHandler

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

            doLoadVideo()
        }
    }

    //endregion

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadView: ${model.identifier}")
        // playTRXlive(model.live)
        // playFMLive()
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
        // finish()
    }

    //region Helper Functions

    //endregion
}
