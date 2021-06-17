package com.trx.consumer.screens.liveplayer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
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
import fm.liveswitch.LocalMedia
import fm.liveswitch.Log
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
    private var localMediaStarted: Boolean = false

    lateinit var container: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_player)

        bind()
    }

    private fun bind() {
        val workout = NavigationManager.shared.params(intent) as? WorkoutModel

        viewModel.apply {
            model = workout

            eventLoadVideo.observe(this@LivePlayerActivity, handleLoadVideo)
        }

        viewModel.doLoadVideo()

        container = findViewById(R.id.container)
        livePlayerHandler.livePlayerActivity = this
    }

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapClose")
        playVideo(container, model.live)
    }

    fun playVideo(view: RelativeLayout, value: LiveResponseModel) {
        livePlayerHandler.start(this, view, value)
        // something()
    }

    fun something() {
        val startFn = IAction0 {
            livePlayerHandler.startLocalMedia(this)
                .then({ o -> livePlayerHandler.joinAsync() }) { e ->
                    Log.error("Could not start local media", e)
                    alert(e.message)
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

    override fun onBackPressed() {
        stop()
        super.onBackPressed()
    }

    private fun stop() {
        if (localMediaStarted) {
            // Future<Object> promise =
            livePlayerHandler.leaveAsync()?.then(IAction1<Any?> { stopLocalMediaAndFinish() })
                ?.fail(
                    IAction1<Exception> { e ->
                        Log.error("Could not leave conference", e)
                        alert(e.message)
                    }
                )
        } else {
            finish()
        }
        localMediaStarted = false
    }

    private fun stopLocalMediaAndFinish() {
        livePlayerHandler.stopLocalMedia()?.then(IAction1<LocalMedia?> { finish() })
            ?.fail(
                IAction1<java.lang.Exception> { e ->
                    Log.error("Could not stop local media", e)
                    alert(e.message)
                }
            )
    }

    fun alert(format: String?, vararg args: Any?) {
        val text = String.format(format!!, *args)
        val activity: Activity = this
        activity.runOnUiThread {
            val alert = AlertDialog.Builder(activity)
            alert.setMessage(text)
            alert.setPositiveButton(
                "OK"
            ) { _, _ -> }
            alert.show()
        }
    }

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
            if (livePlayerHandler.getEnableSimulcast()) {
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
            if (livePlayerHandler.getEnableSimulcast()) {
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

    fun updateRecvEncodingFlag(id: String, bitrate: Int) {
        livePlayerHandler.contextMenuItemFlag.entries.forEach { entry ->
            if (entry.key.contains(id + prefix)) {
                livePlayerHandler.contextMenuItemFlag[entry.key] = entry.key == id + prefix + bitrate
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        livePlayerHandler.livePlayerActivity = null
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
}
