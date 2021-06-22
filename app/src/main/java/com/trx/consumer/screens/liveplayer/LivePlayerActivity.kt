package com.trx.consumer.screens.liveplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import dagger.hilt.android.AndroidEntryPoint
import fm.liveswitch.EncodingInfo
import fm.liveswitch.IAction0
import fm.liveswitch.IAction1
import fm.liveswitch.Log
import fm.liveswitch.Promise
import fm.liveswitch.VideoEncodingConfig
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class LivePlayerActivity : AppCompatActivity() {

    private val viewModel: LivePlayerViewModel by viewModels()

    @Inject
    lateinit var livePlayerHandler: LivePlayerHandler

    private var currentId: String = ""
    private var sendEncodings: ArrayList<Int>? = null
    private var recvEncodings: ArrayList<Int>? = null
    private val prefix = "Bitrate: "

    //  TODO: Place in viewmodel. 
    private var localMediaStarted: Boolean = false

    var container: RelativeLayout? = null
    lateinit var layout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_player)

        bind()
    }

    private fun bind() {
        val workout = NavigationManager.shared.params(intent) as? WorkoutModel

        container = findViewById(R.id.fmPlayerContainer)
        layout = findViewById(R.id.fmPlayerLayout)

        viewModel.apply {
            model = workout
            eventLoadVideo.observe(this@LivePlayerActivity, handleLoadVideo)
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
            layout.removeView(container)
        }

        super.onStop()
    }

    override fun onPause() {
        livePlayerHandler.pauseLocalVideo().waitForResult()

        // Remove the static container from the current layout.
        if (container != null) {
            layout.removeView(container)
        }

        super.onPause()
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
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
                livePlayerHandler.livePlayerActivity = this
                livePlayerHandler.startLocalMedia(this)
                    .then({ o -> livePlayerHandler.joinAsync() }) { e ->
                        LogManager.log("Could not start local media: ${e.message}")
                    }
            } else {
                Toast.makeText(
                    this,
                    "Cannot connect without access to camera and microphone",
                    Toast.LENGTH_SHORT
                ).show()
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.debug("permission to " + permissions[i] + " not granted")
                    }
                }
                stop()
            }
        } else {
            Toast.makeText(this, "Unknown permission requested", Toast.LENGTH_SHORT).show()
        }
    }

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
            updateRecvEncodingFlag(id, recvEncodings!![itemId])
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

    fun playTRXlive(value: LiveResponseModel) {
        livePlayerHandler.useNextVideoDevice()

        livePlayerHandler.livePlayerActivity = this

        val tempContainer = findViewById<RelativeLayout>(R.id.fmPlayerContainer)
        if (container == null) {
            container = tempContainer
        }

        // layout.removeView(tempContainer)

        if (!localMediaStarted) {
            val promise = Promise<Any>()

            val startFn = IAction0 {
                livePlayerHandler.startTRXLocalMedia(this).then({ resultStart ->
                    livePlayerHandler.joinAsyncLive(value)?.then({ resultJoin ->
                        promise.resolve(null)
                    }) { ex ->
                        promise.reject(ex)
                    }
                }) { ex ->
                    promise.reject(null)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requiredPermissions: MutableList<String> = ArrayList(3)
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requiredPermissions.add(Manifest.permission.CAMERA)
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
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

    fun playFMLive() {

        livePlayerHandler.useNextVideoDevice()

        livePlayerHandler.livePlayerActivity = this

        val tempContainer = findViewById<RelativeLayout>(R.id.fmPlayerContainer)
        if (container == null) {
            container = tempContainer
        }
        // layout.removeView(tempContainer)

        if (!localMediaStarted) {
            val promise = Promise<Any>()

            val startFn = IAction0 {
                livePlayerHandler.startLocalMedia(this).then({ resultStart ->
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
                val requiredPermissions: MutableList<String> = ArrayList(3)
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requiredPermissions.add(Manifest.permission.CAMERA)
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
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
            livePlayerHandler.leaveAsync()?.then { stopLocalMediaAndFinish() }
                ?.fail(
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
        livePlayerHandler.stopLocalMedia().then { finish() }
            ?.fail(
                IAction1 { e ->
                    LogManager.log("Could not stop local media: ${e.message}")
                }
            )
    }

    fun updateRecvEncodingFlag(id: String, bitrate: Int) {
        livePlayerHandler.contextMenuItemFlag.entries.forEach { entry ->
            if (entry.key.contains(id + prefix)) {
                livePlayerHandler.contextMenuItemFlag[entry.key] = entry.key == id + prefix + bitrate
            }
        }
    }

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

    private fun getBitrate(encoding: String): Int {
        val str = encoding.split(",").toTypedArray()
        for (i in str.indices) {
            if (str[i].contains(prefix.trim().dropLast(1))) {
                return str[i].split(":").toTypedArray()[1].trim { it <= ' ' }.toInt()
            }
        }
        return 0
    }

    fun testResume() {
        // Add the static container to the current layout.
        if (container != null) {
            layout.addView(container)
        }

        // Resume the local video feed.
        livePlayerHandler.resumeLocalVideo().waitForResult()
    }

    //endregion
}
