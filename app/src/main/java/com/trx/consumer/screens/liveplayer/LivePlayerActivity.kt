package com.trx.consumer.screens.liveplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.common.CommonView
import com.trx.consumer.databinding.ActivityLivePlayerBinding
import com.trx.consumer.extensions.checkLivePermission
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AnalyticsPageModel.LIVE_PLAYER
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import dagger.hilt.android.AndroidEntryPoint
import fm.liveswitch.EncodingInfo
import fm.liveswitch.IAction0
import fm.liveswitch.IAction1
import fm.liveswitch.Promise
import fm.liveswitch.VideoEncodingConfig
import fm.liveswitch.android.OpenGLView
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class LivePlayerActivity : AppCompatActivity() {

    private val viewModel: LivePlayerViewModel by viewModels()
    private lateinit var binding: ActivityLivePlayerBinding
    @Inject
    lateinit var livePlayerHandler: LivePlayerHandler

    private var currentId: String = ""
    private var sendEncodings: ArrayList<Int>? = null
    private var recvEncodings: ArrayList<Int>? = null
    private val prefix = "Bitrate: "

    private var localMediaStarted: Boolean = false

    lateinit var container: CommonView

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLivePlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        doTrackPageView()
        bind()
    }

    private fun bind() {
        val workout = NavigationManager.shared.params(intent) as? WorkoutModel

        container = findViewById(R.id.fmPlayerContainer)

        livePlayerHandler.apply {
            eventTrainerLoaded.observe(this@LivePlayerActivity, addTrainerToLayout)
        }
        val liveResponseModel = LiveResponseModel.test()
        playTRXlive(liveResponseModel)
    }

    private val addTrainerToLayout = Observer<Boolean> { trainerLoaded ->
        LogManager.log("trainerLoaded")
        this.runOnUiThread {
            var trainerMedia = this.livePlayerHandler.getTrainerMediaView()
            if (trainerLoaded) {
                trainerMedia?.let { tm ->
                    tm.view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
                    container.addView(tm.view)
                }
            } else {
                container.removeAllViewsInLayout()
            }
        }
    }



    //region Activity Lifecycle

    override fun onResume() {
        super.onResume()
        viewModel.doLoadVideo()
    }

    override fun onStop() {

        // Android requires us to pause the local
        // video feed when pausing the activity.
        // Not doing this can cause unexpected side
        // effects and crashes.

        livePlayerHandler.pauseLocalVideo().waitForResult()

        // Remove the static container from the current layout.
        if (container != null) {
            //layout.removeView(container)
        }

        super.onStop()
    }

    override fun onPause() {
        livePlayerHandler.pauseLocalVideo().waitForResult()

        // Remove the static container from the current layout.
        if (container != null) {
            //layout.removeView(container)
        }

        super.onPause()
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    //endregion 

    //region Activity Helper Overrides

    override fun onBackPressed() {
        stop()
        super.onBackPressed()
    }

    //  TODO: Marked for removal, Plans to use physical buttons instead of context.
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        container = findViewById(R.id.container)

        val id = v.contentDescription.toString()
        currentId = id
        var index = 0
        if (id == "localView") {
            menu.setHeaderTitle("Local")
            val muteAudio = menu.add(0, 0, 0, "Mute Audio")
            val muteVideo = menu.add(0, 1, 0, "Mute Video")
            val disableAudio = menu.add(0, 2, 0, "Disable Audio")
            val disableVideo = menu.add(0, 3, 0, "Disable Video")
            menu.setGroupCheckable(0, true, false)
            muteAudio.isChecked = livePlayerHandler.contextMenuItemFlag["MuteAudio"] ?: false
            muteVideo.isChecked = livePlayerHandler.contextMenuItemFlag["MuteVideo"] ?: false
            disableAudio.isChecked = livePlayerHandler.contextMenuItemFlag["DisableAudio"] ?: false
            disableVideo.isChecked = livePlayerHandler.contextMenuItemFlag["DisableVideo"] ?: false
            if (livePlayerHandler.enableSimulcast) {
                sendEncodings?.let { send ->
                    send.sort()
                    send.reverse()
                    val encodings = menu.addSubMenu(0, 2, 0, "Video Encoding")
                    for (bitrate in send) {
                        val item: MenuItem = encodings.add(1, index, 0, prefix + bitrate)
                        item.isChecked =
                            livePlayerHandler.contextMenuItemFlag[id + prefix + bitrate] ?: false
                        index++
                    }
                    encodings.setGroupCheckable(1, true, false)
                }
            }
        } else {
            menu.setHeaderTitle("Remote")
            val disableAudio = menu.add(2, 0, 0, "Disable Audio")
            val disableVideo = menu.add(2, 1, 0, "Disable Video")
            menu.setGroupCheckable(2, true, false)
            disableAudio.isChecked = livePlayerHandler.contextMenuItemFlag[id + "DisableAudio"] ?: false
            disableVideo.isChecked = livePlayerHandler.contextMenuItemFlag[id + "DisableVideo"] ?: false
            if (livePlayerHandler.enableSimulcast) {
                // Refresh the recvEncoding List in case of each remote media has different encoding

                recvEncodings?.let { receive ->
                    receive.clear()
                    livePlayerHandler.contextMenuItemFlag.entries.forEach { entry ->
                        if (entry.key.contains(id + prefix)) {
                            receive.add(
                                entry.key.split(":").toTypedArray()[1].trim { it <= ' ' }
                                    .toInt()
                            )
                        }
                    }

                    receive.sort()
                    receive.reverse()

                    val encodings = menu.addSubMenu(2, 2, 0, "Video Encoding")
                    for (bitrate in receive) {
                        val item: MenuItem = encodings.add(3, index, 0, prefix + bitrate)
                        item.isChecked =
                            livePlayerHandler.contextMenuItemFlag[id + prefix + bitrate] ?: false
                        index++
                    }

                    encodings.setGroupCheckable(3, true, false)
                }
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id: String = currentId
        val itemId = item.itemId
        if (item.groupId == 0) {
            when (item.itemId) {
                0 -> livePlayerHandler.toggleMuteAudio()
                1 -> livePlayerHandler.toggleMuteVideo()
                2 -> livePlayerHandler.toggleLocalDisableAudio()
                3 -> livePlayerHandler.toggleLocalDisableVideo()
            }
        }
        if (item.groupId == 2) {
            when (item.itemId) {
                0 -> livePlayerHandler.toggleRemoteDisableAudio(id)
                1 -> livePlayerHandler.toggleRemoteDisableVideo(id)
            }
        }
        if (item.groupId == 1) {
            // toggleSendEncoding on local media
            livePlayerHandler.changeSendEncodings(itemId)
            livePlayerHandler.contextMenuItemFlag[id + prefix + sendEncodings!![itemId]] =
                !livePlayerHandler.contextMenuItemFlag[id + prefix + sendEncodings!![itemId]]!!
        }
        if (item.groupId == 3) {
            // toggleRecvEncoding on selected remote media
            livePlayerHandler.changeReceiveEncodings(id, itemId)
            // updateRecvEncodingFlag(id, recvEncodings!![itemId])
        }
        return true
    }

    //endregion

    //region Handlers

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapClose")
        playTRXlive(model.live)
        // playFMLive()
    }

    //endregion

    //region Helper Functions

    private fun playTRXlive(value: LiveResponseModel) {
        if (!localMediaStarted) {
            val promise = Promise<Any>()

            val startFn = IAction0 {
                livePlayerHandler.startTRXLocalMedia().then({ resultStart ->
                    livePlayerHandler.joinAsync()?.then({ resultJoin ->
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

    private fun stop() {
        if (localMediaStarted) {
            livePlayerHandler.leaveAsync()?.then {
                stopLocalMediaAndFinish()
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

    private fun stopLocalMediaAndFinish() {
        livePlayerHandler.stopLocalMedia().then {
            finish()
        }?.fail(
            IAction1 { e ->
                LogManager.log("Could not stop local media: ${e.message}")
            }
        )
    }

    //  TODO: Marked for removal. Keep for reference.
    fun updateRecvEncodingFlag(id: String, bitrate: Int) {
        livePlayerHandler.contextMenuItemFlag.entries.forEach { entry ->
            if (entry.key.contains(id + prefix)) {
                livePlayerHandler.contextMenuItemFlag[entry.key] = entry.key == id + prefix + bitrate
            }
        }
    }

    //  TODO: Marked for removal. Keep for reference.
    fun registerLocalContextMenu(view: View, encodings: Array<VideoEncodingConfig>?) {
        val id = view.contentDescription.toString()
        sendEncodings = ArrayList()
        livePlayerHandler.contextMenuItemFlag["MuteAudio"] = false
        livePlayerHandler.contextMenuItemFlag["MuteVideo"] = false
        livePlayerHandler.contextMenuItemFlag["DisableAudio"] = false
        livePlayerHandler.contextMenuItemFlag["DisableVideo"] = false
        if (encodings != null && encodings.size > 1) {
            for (i in encodings.indices) {
                val bitrate: Int = getBitrate(encodings[i].toString())
                sendEncodings?.add(bitrate)
                livePlayerHandler.contextMenuItemFlag[id + prefix + bitrate] = true
            }
        }
        registerForContextMenu(view)
    }

    //  TODO: Marked for removal. Keep for reference.
    fun registerRemoteContextMenu(view: View, encodings: Array<EncodingInfo>?) {
        val id = view.contentDescription.toString()
        recvEncodings = ArrayList()
        livePlayerHandler.contextMenuItemFlag[id + "DisableAudio"] = false
        livePlayerHandler.contextMenuItemFlag[id + "DisableVideo"] = false
        if (encodings != null && encodings.size > 1) {
            for (i in encodings.indices) {
                val bitrate: Int = getBitrate(encodings[i].toString())
                recvEncodings?.add(bitrate)
                livePlayerHandler.contextMenuItemFlag[id + prefix + bitrate] = i == 0
            }
        }
        registerForContextMenu(view)
    }

    //  TODO: Marked for removal. Keep for reference.
    private fun getBitrate(encoding: String): Int {
        val str = encoding.split(",").toTypedArray()
        for (i in str.indices) {
            if (str[i].contains(prefix.trim().dropLast(1))) {
                return str[i].split(":").toTypedArray()[1].trim { it <= ' ' }.toInt()
            }
        }
        return 0
    }

    //endregion

    fun doTrackPageView() {
        analyticsManager.trackPageView(LIVE_PLAYER)
    }
}
