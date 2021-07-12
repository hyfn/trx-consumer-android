package com.trx.consumer.screens.groupplayer

import android.content.Context
import android.view.View
import com.trx.consumer.BuildConfig.kFMApplicationIdProd
import com.trx.consumer.BuildConfig.kFMGatewayUrl
import com.trx.consumer.frozenmountain.AecContext
import com.trx.consumer.frozenmountain.LocalCameraMedia
import com.trx.consumer.frozenmountain.LocalMedia
import com.trx.consumer.frozenmountain.RemoteMedia
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.responses.LiveResponseModel
import fm.liveswitch.AudioStream
import fm.liveswitch.Channel
import fm.liveswitch.Client
import fm.liveswitch.ClientState.Unregistered
import fm.liveswitch.ConnectionConfig
import fm.liveswitch.ConnectionInfo
import fm.liveswitch.ConnectionState
import fm.liveswitch.DataChannel
import fm.liveswitch.DataChannelState
import fm.liveswitch.DataStream
import fm.liveswitch.Future
import fm.liveswitch.Guid
import fm.liveswitch.IAction0
import fm.liveswitch.IAction1
import fm.liveswitch.LayoutUtility
import fm.liveswitch.License
import fm.liveswitch.Log
import fm.liveswitch.ManagedConnection
import fm.liveswitch.ManagedThread
import fm.liveswitch.ManagedTimer
import fm.liveswitch.McuConnection
import fm.liveswitch.PeerConnection
import fm.liveswitch.PeerConnectionOffer
import fm.liveswitch.Promise
import fm.liveswitch.SfuDownstreamConnection
import fm.liveswitch.SfuUpstreamConnection
import fm.liveswitch.SimulcastMode
import fm.liveswitch.StreamDirection
import fm.liveswitch.VideoLayout
import fm.liveswitch.VideoStream
import fm.liveswitch.android.Camera2Source
import fm.liveswitch.android.LayoutManager
import fm.liveswitch.openh264.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class GroupPlayerHandler(val context: Context) {

    private var channel: Channel? = null

    var handlerScope: CoroutineScope? = null
    private var mcuConnection: McuConnection? = null
    private var sfuUpstreamConnection: SfuUpstreamConnection? = null
    private var sfuDownstreamConnections: HashMap<String, SfuDownstreamConnection>
    private var peerConnections: HashMap<String, PeerConnection>
    private var localMedia: LocalMedia<View>? = null
    private lateinit var aecContext: AecContext
    private var layoutManager: LayoutManager? = null
    private var videoLayout: VideoLayout? = null
    var groupPlayerActivity: GroupPlayerActivity? = null
    var contextMenuItemFlag: HashMap<String, Boolean> = HashMap()
    var remoteMediaMaps: HashMap<String, ManagedConnection>

    //region Client Variables and Parameters
    private var client: Client? = null

    //endregion

    // Track whether the user has decided to leave (unregister)
    // If they have not and the client gets into the Disconnected state then we attempt to reregister (reconnect) automatically.
    private var unRegistering = false
    private var reRegisterBackoff = 200
    private val maxRegisterBackoff = 60000

    private var isModeMcu: Boolean = false

    private lateinit var mcuViewId: String

    var listener: GroupPlayerListener? = null
    private var audioOnly: Boolean = false
    private var receiveOnly: Boolean = false
    var enableSimulcast = false

    val enableH264: Boolean
        get() = Utility.isSupported()

    private var dataChannelConnected = false
    var dataChannels = ArrayList<DataChannel>()
    private val dataChannelLock: Any =
        // synchronize data channel book-keeping (collection may be
        // modified while trying to send messages in SendDataChannelMessage())
        Object()

    init {
        contextMenuItemFlag = HashMap()
        remoteMediaMaps = HashMap()
        sfuDownstreamConnections = HashMap()
        peerConnections = HashMap()
        License.getCurrent()
    }

    //region GroupPlayerActivity Function

    fun start(live: LiveResponseModel) {
        val promise = Promise<Any>()

        startLocalMedia().then({
            joinAsync(live)?.then({
                promise.resolve(null)
            }) { ex ->
                ex.message?.let { safeMessage -> LogManager.log(safeMessage) }
                promise.reject(ex)
            }
        }) { ex ->
            ex.message?.let { safeMessage -> LogManager.log(safeMessage) }
            promise.reject(null)
        }
    }

    private fun startLocalMedia(): Future<Any> {
        val promise = Promise<Any>()

        // Set up the layout manager.
        // Create an echo cancellation context w/ AecContext
        // Set up the local media.
        // Change input source to front camera.
        groupPlayerActivity?.let { activity ->
            layoutManager = LayoutManager(activity.container)

            handlerScope?.launch(Dispatchers.Main) {
                if (receiveOnly) {
                    promise.resolve(null)
                } else {
                    aecContext = AecContext()

                    localMedia = LocalCameraMedia(
                        context,
                        enableH264,
                        false,
                        audioOnly,
                        aecContext,
                        enableSimulcast
                    ).apply {
                        view.contentDescription = "localView"
                        layoutManager?.localView = view
                    }

                    (localMedia?.videoSource as? Camera2Source)?.let { input ->
                        input.frontInput?.let { sourceInput ->
                            localMedia?.changeVideoSourceInput(sourceInput)
                        }
                    }

                    // Start the local media.
                    localMedia?.let { safeLocalMedia ->
                        safeLocalMedia.start().then({
                            promise.resolve(null)
                        }) { e ->
                            promise.reject(e)
                        }
                    }
                }
            }
        }
        return promise
    }

    private fun joinAsync(live: LiveResponseModel): Future<Array<Channel>>? {
        unRegistering = false

        // Hardcoded
        val deviceID: String = Guid.newGuid().toString().replace("-".toRegex(), "")
        client = Client(kFMGatewayUrl, kFMApplicationIdProd, live.sessionCustomerUid, deviceID)
            .apply {
                userAlias = live.participantName
                addOnStateChange { safeClient ->
                    LogManager.log("Client state is: ${safeClient.state.name} ")
                    if (safeClient.state == Unregistered) {
                        if (!unRegistering) {
                            ManagedThread.sleep(maxRegisterBackoff)
                            if (maxRegisterBackoff < maxRegisterBackoff) {
                                reRegisterBackoff += reRegisterBackoff
                            }

                            safeClient.register(live.accessToken).then({ channels ->
                                reRegisterBackoff = 200
                                onClientRegistered(channels)
                            }) { e ->
                                LogManager.log("Failed to reregister with Gateway. ${e.message}")
                            }
                        }
                    }
                }
            }

        return client?.register(live.accessToken)?.then({ channels ->
            onClientRegistered(channels)
        }) { e ->
            LogManager.log("Failed to register with Gateway. ${e.message}")
        }
    }

    fun cleanup(): Future<fm.liveswitch.LocalMedia?> {
        val promise: Promise<fm.liveswitch.LocalMedia?> = Promise()
        groupPlayerActivity = null
        listener = null
        handlerScope = null
        clearContextMenuItemFlag("localView")
        if (localMedia == null) {
            promise.resolve(null)
        } else {
            localMedia!!.stop().then({ // Tear down the layout manager.
                layoutManager?.let { safeLayoutManager ->
                    safeLayoutManager.apply {
                        removeRemoteViews()
                        unsetLocalView()
                    }
                }
                layoutManager = null

                // Tear down the local media.
                localMedia?.destroy()
                localMedia = null

                promise.resolve(null)
            }) { e -> promise.reject(e) }
        }
        return promise
    }

    fun leaveAsync(): Future<Any>? {
        client?.let { safeClient ->
            unRegistering = true

            // Unregister with the server.
            return safeClient.unregister().then {
                dataChannelConnected = false
            }.fail(
                IAction1 { e ->
                    e.message?.let { LogManager.log("Failed to Unregister Client: $it") }
                }
            )
        }

        return null
    }

    //endregion

    private var dataChannelsMessageTimer: ManagedTimer? = null

    // Used when opening Mcu connection and PeerAnswerConnection.
    private fun addRemoteViewOnUiThread(remoteMedia: RemoteMedia) {
        layoutManager?.let { safeLayoutManager ->
            handlerScope?.launch(Dispatchers.Main) {
                remoteMedia.view?.let { safeView ->
                    safeView.contentDescription = "remoteView_${safeView.id}"
                    safeLayoutManager.addRemoteView(remoteMedia.id, remoteMedia.view)
                }
            }
        }
    }

    // Used when opening Mcu connection and PeerAnswerConnection.
    private fun removeRemoteViewOnUiThread(remoteMedia: RemoteMedia) {
        layoutManager?.let { safeLayoutManager ->
            clearContextMenuItemFlag(remoteMedia.id)
            handlerScope?.launch(Dispatchers.Main) {
                safeLayoutManager.removeRemoteView(remoteMedia.id)
                remoteMedia.destroy()
            }
        }
    }

    // Used in onClientRegistered
    private fun layoutOnUiThread() {
        layoutManager?.layoutOnMainThread()
    }

    private fun onClientRegistered(channels: Array<Channel>) {
        channel = channels.firstOrNull()

        // Monitor the channel remote client changes.
        channel?.addOnRemoteClientJoin { remoteClientInfo ->
            LogManager.log(
                "Remote client joined the channel (client ID: ${remoteClientInfo.id}, " +
                    "device ID:  ${remoteClientInfo.deviceId}, " +
                    "user ID: ${remoteClientInfo.userId}, " +
                    "tag: ${remoteClientInfo.tag})."
            )
            val user = remoteClientInfo.userAlias ?: remoteClientInfo.userId
            listener?.doReceiveMessage(user, "Joined")
        }
        channel?.addOnRemoteClientLeave { remoteClientInfo ->
            LogManager.log(
                "Remote client left the channel (" +
                    "client ID: ${remoteClientInfo.id}, " +
                    "device ID:  ${remoteClientInfo.deviceId}, " +
                    "user ID: ${remoteClientInfo.userId}, " +
                    "tag: ${remoteClientInfo.tag})."
            )
            val user = remoteClientInfo.userAlias ?: remoteClientInfo.userId
            listener?.doReceiveMessage(user, "Left")
        }

        // Monitor the channel remote upstream connection changes.
        channel?.addOnRemoteUpstreamConnectionOpen { remoteConnectionInfo ->
            LogManager.log(
                "Remote client opened upstream connection (" +
                    "connection ID: ${remoteConnectionInfo.id}, " +
                    "client ID: ${remoteConnectionInfo.clientId}, " +
                    "device ID: ${remoteConnectionInfo.deviceId}, " +
                    "user ID: ${remoteConnectionInfo.userId}, " +
                    "tag: ${remoteConnectionInfo.tag})."
            )
            // Open downstream connection to receive the new upstream connection if mode not Mcu
            if (!isModeMcu) openSfuDownstreamConnection(remoteConnectionInfo, null)
        }
        channel?.addOnRemoteUpstreamConnectionClose { remoteConnectionInfo ->
            LogManager.log(
                "Remote client closed upstream connection (" +
                    "connection ID: ${remoteConnectionInfo.id}, " +
                    "client ID: ${remoteConnectionInfo.clientId}, " +
                    "device ID: ${remoteConnectionInfo.deviceId}, " +
                    "user ID: ${remoteConnectionInfo.userId}, " +
                    "tag: ${remoteConnectionInfo.tag})."
            )
        }

        // Monitor the channel peer connection offers.
        channel?.addOnPeerConnectionOffer { peerConnectionOffer -> // Accept the peer connection offer.
            openPeerAnswerConnection(peerConnectionOffer)
        }
        channel?.addOnMessage { clientInfo, message ->
            val user = clientInfo.userAlias ?: clientInfo.userId
            listener?.doReceiveMessage(user, message)
        }
        if (isModeMcu) {

            // Monitor the channel video layout changes.
            channel?.addOnMcuVideoLayout { vidLayout ->
                if (!receiveOnly) {
                    videoLayout = vidLayout
                    // Force a layout in case the local video preview needs to move.
                    layoutOnUiThread()
                }
            }

            // Open an MCU connection.
            openMcuConnection(null)
        } else {
            if (!receiveOnly) {
                // Open an upstream SFU connection.
                openSfuUpstreamConnection(null)
            }

            // Open a downstream SFU connection for each remote upstream connection.
            channel?.let {
                it.remoteUpstreamConnectionInfos.forEach { connection ->
                    openSfuDownstreamConnection(connection, null)
                }
            }
        }
    }

    private fun openMcuConnection(tag: String?): McuConnection? {
        // Create remote media to manage incoming media.
        val remoteMedia = RemoteMedia(
            context = context,
            disableAudio = false,
            disableVideo = audioOnly,
            aecContext = aecContext
        )
        mcuViewId = remoteMedia.id

        // Add the remote video view to the layout.
        addRemoteViewOnUiThread(remoteMedia)
        val connection: McuConnection?
        val dataChannel = prepareDataChannel()
        val dataStream = DataStream(dataChannel)
        synchronized(dataChannelLock) { dataChannels.add(dataChannel) }
        val audioStream = AudioStream(localMedia, remoteMedia).apply {
            if (receiveOnly) localDirection = StreamDirection.ReceiveOnly
        }

        if (audioOnly) {
            connection = channel?.createMcuConnection(audioStream, dataStream)
        } else {
            val videoStream = VideoStream(localMedia, remoteMedia)
            if (receiveOnly) {
                videoStream.localDirection = StreamDirection.ReceiveOnly
            } else if (enableSimulcast) {
                videoStream.simulcastMode = SimulcastMode.RtpStreamId
            }
            connection = channel?.createMcuConnection(audioStream, videoStream, dataStream)
            connection?.info?.videoStream?.sendEncodings?.let { safeRemoteEncodings ->
                if (safeRemoteEncodings.isNotEmpty()) {
                    videoStream.remoteEncoding = safeRemoteEncodings[0]
                }
            }
        }
        mcuConnection = connection

        // Tag the connection (optional).
        tag?.let { safeTag -> connection?.tag = safeTag }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection?.addOnStateChange {
            Log.info(connection.id + ": MCU connection state is " + connection.state.toString() + ".")

            // Cleanup if the connection closes or fails.
            if (connection.state == ConnectionState.Closing || connection.state == ConnectionState.Failing) {
                if (connection.remoteClosed) {
                    Log.info(connection.id + ": Media server closed the connection.")
                }
                removeRemoteViewOnUiThread(remoteMedia)
                synchronized(dataChannelLock) { dataChannels.remove(dataChannel) }
                logConnectionState(connection, "MCU")
                mcuConnection = null
            } else if (connection.state == ConnectionState.Failed) {
                // Note: no need to close the connection as it's done for us.
                openMcuConnection(tag)
                logConnectionState(connection, "MCU")
            } else if (connection.state == ConnectionState.Connected) {
                logConnectionState(connection, "MCU")
            }
        }

        // Float the local preview over the mixed video feed for an improved user experience.
        layoutManager?.addOnLayout { layout ->
            mcuConnection?.let { mcu ->
                if (!receiveOnly && !audioOnly) {
                    LayoutUtility.floatLocalPreview(
                        layout,
                        videoLayout,
                        mcu.id,
                        mcuViewId,
                        localMedia?.viewSink
                    )
                }
            }
        }

        // Open the connection.
        connection?.open()
        return connection
    }

    private fun openSfuUpstreamConnection(tag: String?): SfuUpstreamConnection? {
        // Create the connection.
        val connection: SfuUpstreamConnection?
        val dataChannel = prepareDataChannel()
        val dataStream = DataStream(dataChannel)
        synchronized(dataChannelLock) { dataChannels.add(dataChannel) }
        var videoStream: VideoStream? = null
        var audioStream: AudioStream? = null
        if (localMedia?.audioTrack != null) {
            audioStream = AudioStream(localMedia)
        }
        if (localMedia?.videoTrack != null) {
            videoStream = VideoStream(localMedia)
            if (enableSimulcast) {
                videoStream.simulcastMode = SimulcastMode.RtpStreamId
            }
        }
        connection = channel?.createSfuUpstreamConnection(audioStream, videoStream, dataStream)
        connection?.let {
            sfuUpstreamConnection = it
        }

        // Tag the connection (optional).
        tag?.let { safeTag -> connection?.tag = safeTag }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection?.addOnStateChange {
            Log.info(connection.id + ": SFU upstream connection state is " + connection.state.toString() + ".")

            // Cleanup if the connection closes or fails.
            if (connection.state == ConnectionState.Closing || connection.state == ConnectionState.Failing) {
                if (connection.remoteClosed) {
                    Log.info(connection.id + ": Media server closed the connection.")
                }
                sfuUpstreamConnection = null
                synchronized(dataChannelLock) { dataChannels.remove(dataChannel) }
                logConnectionState(connection, "SFU Upstream")
            } else if (connection.state == ConnectionState.Failed) {
                // Note: no need to close the connection as it's done for us.
                openSfuUpstreamConnection(tag)
                logConnectionState(connection, "SFU Upstream")
            } else if (connection.state == ConnectionState.Connected) {
                logConnectionState(connection, "SFU Upstream")
            }
        }

        // Open the connection.
        connection?.open()
        return connection
    }

    private fun openSfuDownstreamConnection(
        remoteConnectionInfo: ConnectionInfo,
        tag: String?
    ): SfuDownstreamConnection? {
        // Create remote media to manage incoming media.
        val remoteMedia = RemoteMedia(
            context = context,
            disableAudio = false,
            disableVideo = audioOnly,
            aecContext = aecContext
        )

        // Add the remote video view to the layout.
        handlerScope?.launch(Dispatchers.Main) {
            layoutManager?.addRemoteView(
                remoteMedia.id,
                remoteMedia.view
            )
        }

        var videoStream: VideoStream? = null
        var audioStream: AudioStream? = null
        if (remoteConnectionInfo.hasAudio) {
            audioStream = AudioStream(null, remoteMedia)
        }
        if (remoteConnectionInfo.hasVideo && !audioOnly) {
            videoStream = VideoStream(null, remoteMedia)
            // TODO: Mark for removal unless using simulcast
            // if (enableSimulcast) {
            //     val remoteEncodings = remoteConnectionInfo.videoStream.sendEncodings
            //     if (remoteEncodings != null && remoteEncodings.size > 0) {
            //         videoStream!!.remoteEncoding = remoteEncodings[0]
            //     }
            // }
        }

        // Create SfuDownstreamConnection, store in maps and add tag. 
        val connection: SfuDownstreamConnection? = channel?.createSfuDownstreamConnection(
            remoteConnectionInfo,
            audioStream,
            videoStream
        )?.apply {
            sfuDownstreamConnections[remoteMedia.id] = this
            remoteMediaMaps[remoteMedia.id] = this
            tag?.let { newTag -> this.tag = newTag }
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection?.addOnStateChange { safeConnection ->
            Log.info(safeConnection.id + ": SFU downstream connection state is " + safeConnection.state.toString() + ".")
            // Cleanup if the connection closes or fails.
            when (safeConnection.state) {
                ConnectionState.Connected -> {
                    logConnectionState(safeConnection, "SFU Downstream")
                }
                ConnectionState.Closing, ConnectionState.Failing -> {
                    if (safeConnection.remoteClosed) {
                        Log.info(connection.id + ": Media server closed the connection.")
                    }

                    layoutManager?.addRemoteView(
                        remoteMedia.id,
                        remoteMedia.view
                    )

                    handlerScope?.launch {
                        layoutManager?.removeRemoteView(remoteMedia.id)
                        remoteMedia.destroy()
                    }

                    sfuDownstreamConnections.remove(remoteMedia.id)
                    remoteMediaMaps.remove(remoteMedia.id)
                    logConnectionState(safeConnection, "SFU Downstream")
                }
                ConnectionState.Failed -> {
                    // Note: no need to close the connection as it's done for us.
                    openSfuDownstreamConnection(remoteConnectionInfo, tag)
                    logConnectionState(safeConnection, "SFU Downstream")
                }
                else -> {
                    LogManager.log("Not logging state: ${safeConnection.state.name}")
                }
            }
        }

        // Open the connection.
        connection?.open()
        return connection
    }

    private fun openPeerAnswerConnection(
        peerConnectionOffer: PeerConnectionOffer
    ): PeerConnection? {
        // Create remote media to manage incoming media.
        var disableRemoteVideo = audioOnly
        if (peerConnectionOffer.hasVideo) {
            /*
            The remote client is offering audio AND video => they are expecting a VideoStream in the connection.
            To create this connection successfully we must include a VideoStream, even though we may have chosen to be in audio only mode.
            In this case we simply set the VideoStream's direction to inactive.
            */
            disableRemoteVideo = false
        }

        val remoteMedia = RemoteMedia(
            context = context,
            disableAudio = false,
            disableVideo = disableRemoteVideo,
            aecContext = aecContext
        )

        // Add the remote video view to the layout.
        addRemoteViewOnUiThread(remoteMedia)
        remoteMedia.view?.let { remoteView ->
            remoteView.contentDescription = "remoteView_" + remoteMedia.id
        }

        val connection: PeerConnection?
        val videoStream: VideoStream? = if (peerConnectionOffer.hasVideo) {
            VideoStream(localMedia, remoteMedia).apply {
                if (audioOnly) {
                    localDirection = StreamDirection.Inactive
                }
            }
        } else null

        val audioStream: AudioStream? = if (peerConnectionOffer.hasAudio) {
            AudioStream(localMedia, remoteMedia)
        } else null

        // Please note that DataStreams can also be added to Peer-to-peer connections.
        // Nevertheless, since peer connections do not connect to the media server, there may arise
        // incompatibilities with the peers that do not support DataStream (e.g. Microsoft Edge browser:
        // https://developer.microsoft.com/en-us/microsoft-edge/platform/status/rtcdatachannels/?filter=f3f0000bf&search=rtc&q=data%20channels).
        // For a solution around this issue and complete documentation visit:
        // https://help.frozenmountain.com/docs/liveswitch1/working-with-datachannels

        connection = channel?.createPeerConnection(
            peerConnectionOffer,
            audioStream,
            videoStream
        )?.apply {
            peerConnections[id] = this
            remoteMediaMaps[id] = this
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.

        connection?.let { safeConnect ->
            LogManager.log("${safeConnect.id}: Peer connection state is ${connection.state}.")

            safeConnect.addOnStateChange {
                when (safeConnect.state) {
                    ConnectionState.Connected -> {
                        logConnectionState(connection, "Peer")
                    }
                    // Cleanup if the connection closes or fails.
                    ConnectionState.Failing, ConnectionState.Closing -> {
                        if (connection.remoteClosed) {
                            LogManager.log("${connection.id}: Remote peer closed the connection.")
                        }
                        removeRemoteViewOnUiThread(remoteMedia)
                        peerConnections.remove(safeConnect.id)
                        remoteMediaMaps.remove(remoteMedia.id)
                        logConnectionState(safeConnect, "Peer")
                    }
                    ConnectionState.Failed -> {
                        logConnectionState(safeConnect, "Peer")
                    }
                    else -> { LogManager.log("Not logging state: ${safeConnect.state.name}") }
                }
            }
        }

        // Open the connection (sends an answer to the remote peer).
        connection?.open()
        return connection
    }

    private fun logConnectionState(conn: ManagedConnection, connectionType: String) {
        var streams = ""
        var streamCount = 0
        if (conn.audioStream != null) {
            streamCount++
            streams = "audio"
        }
        if (conn.dataStream != null) {
            if (streams.isNotEmpty()) {
                streams += "/"
            }
            streamCount++
            streams += "data"
        }
        if (conn.videoStream != null) {
            if (streams.isNotEmpty()) {
                streams += "/"
            }
            streamCount++
            streams += "video"
        }
        streams += if (streamCount > 1) {
            " streams."
        } else {
            " stream."
        }
        when (conn.state) {
            ConnectionState.Connected -> {
                listener?.doReceiveMessage(
                    "System",
                    "$connectionType connection connected with $streams"
                )
            }
            ConnectionState.Closing -> {
                listener?.doReceiveMessage(
                    "System",
                    "$connectionType connection closing for $streams"
                )
            }
            ConnectionState.Failing -> {
                var eventString: String? = "$connectionType connection failing for $streams"
                if (conn.error != null) {
                    eventString += conn.error.description
                }
                eventString?.let {
                    listener?.doReceiveMessage("System", it)
                }
            }
            ConnectionState.Closed -> {
                listener?.doReceiveMessage(
                    "System",
                    "$connectionType connection closed for $streams"
                )
            }
            ConnectionState.Failed -> {
                listener?.doReceiveMessage(
                    "System",
                    "$connectionType connection failed for $streams"
                )
            }
            else -> { LogManager.log("Not logging state: ${conn.state.name}") }
        }
    }

    private fun sendMessageInDataChannels(): IAction0 {
        return IAction0 {
            var channels: Array<DataChannel>
            synchronized(dataChannelLock) { channels = dataChannels.toTypedArray() }
            for (channel in channels) {
                channel.sendDataString("Hello world!")
            }
        }
    }

    private fun prepareDataChannel(): DataChannel {
        val dataChannel = DataChannel("data")
        dataChannel.onReceive = IAction1 { dataChannelReceiveArgs ->
            if (!dataChannelConnected) {
                dataChannelReceiveArgs.dataString?.let { dataString ->
                    listener?.doReceiveMessage(
                        "System",
                        "Data channel connection established." +
                            " Received test message from server: " +
                            dataString
                    )
                }
                dataChannelConnected = true
            }
        }
        dataChannel.addOnStateChange { channel ->
            if (channel.state == DataChannelState.Connected) {
                dataChannelsMessageTimer = ManagedTimer(1000, sendMessageInDataChannels())
                dataChannelsMessageTimer?.start()
                // if (dataChannelsMessageTimer == null) {
                //     dataChannelsMessageTimer = ManagedTimer(1000, sendMessageInDataChannels())
                //     dataChannelsMessageTimer!!.start()
                // }
            }
        }
        return dataChannel
    }

    fun changeSendEncodings(index: Int) {
        val encodings = localMedia?.videoEncodings
        encodings?.let { safeEncodings ->
            safeEncodings[index].deactivated = !safeEncodings[index].deactivated
            localMedia?.videoEncodings = safeEncodings
        }
    }

    fun changeReceiveEncodings(id: String, index: Int) {
        val connection = sfuDownstreamConnections[
            id.replace("remoteView_", "").trim { it <= ' ' }
        ]
        val encodings = connection!!.remoteConnectionInfo.videoStream.sendEncodings
        if (encodings != null && encodings.size > 1) {
            val config = connection.config
            config.remoteVideoEncoding = encodings[index]
            connection.update(config)
                .then {
                    Log.debug("Updated video encoding to: " + encodings[index] + " for connection: " + connection)
                }
                .fail(
                    IAction1 { ex: Exception? ->
                        Log.error(
                            "Could not change video stream encoding for connection: $connection",
                            ex
                        )
                    }
                )
        }
    }

    fun toggleMuteAudio() {
        var config: ConnectionConfig? = null
        if (sfuUpstreamConnection != null) {
            config = sfuUpstreamConnection!!.config
            config.localAudioMuted = !config.localAudioMuted
            sfuUpstreamConnection!!.update(config)
        }
        if (mcuConnection != null) {
            config = mcuConnection!!.config
            config.localAudioMuted = !config.localAudioMuted
            mcuConnection!!.update(config)
        }
        for (peerConnection in peerConnections.values) {
            config = peerConnection.config
            config.localAudioMuted = !config.localAudioMuted
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag["MuteAudio"] = config.localAudioMuted
        }
    }

    fun toggleMuteVideo() {
        var config: ConnectionConfig? = null
        if (sfuUpstreamConnection != null) {
            config = sfuUpstreamConnection!!.config
            config.localVideoMuted = !config.localVideoMuted
            sfuUpstreamConnection!!.update(config)
        }
        if (mcuConnection != null) {
            config = mcuConnection!!.config
            config.localVideoMuted = !config.localVideoMuted
            mcuConnection!!.update(config)
        }
        for (peerConnection in peerConnections.values) {
            config = peerConnection.config
            config.localVideoMuted = !config.localVideoMuted
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag["MuteVideo"] = config.localVideoMuted
        }
    }

    fun toggleLocalDisableAudio() {
        var config: ConnectionConfig? = null
        if (sfuUpstreamConnection != null) {
            config = sfuUpstreamConnection!!.config
            config.localAudioDisabled = !config.localAudioDisabled
            sfuUpstreamConnection!!.update(config)
        }
        if (mcuConnection != null) {
            config = mcuConnection!!.config
            config.localAudioDisabled = !config.localAudioDisabled
            mcuConnection!!.update(config)
        }
        for (peerConnection in peerConnections.values) {
            config = peerConnection.config
            config.localAudioDisabled = !config.localAudioDisabled
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag["DisableAudio"] = config.localAudioDisabled
        }
    }

    fun toggleRemoteDisableAudio(remoteId: String) {
        val id = remoteId.replace("remoteView_", "")
        val downStream = remoteMediaMaps[id]
        val config = downStream!!.config
        config.remoteAudioDisabled = !config.remoteAudioDisabled
        contextMenuItemFlag[remoteId + "DisableAudio"] = config.remoteAudioDisabled
        downStream.update(config)
    }

    fun toggleLocalDisableVideo() {
        var config: ConnectionConfig? = null
        if (sfuUpstreamConnection != null) {
            config = sfuUpstreamConnection!!.config
            config.localVideoDisabled = !config.localVideoDisabled
            sfuUpstreamConnection!!.update(config)
        }
        if (mcuConnection != null) {
            config = mcuConnection!!.config
            config.localVideoDisabled = !config.localVideoDisabled
            mcuConnection!!.update(config)
        }
        for (peerConnection in peerConnections.values) {
            config = peerConnection.config
            config.localVideoDisabled = !config.localVideoDisabled
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag["DisableVideo"] = config.localVideoDisabled
        }
    }

    fun toggleRemoteDisableVideo(remoteId: String) {
        val id = remoteId.replace("remoteView_", "")
        val downStream = remoteMediaMaps[id]
        val config = downStream!!.config
        config.remoteVideoDisabled = !config.remoteVideoDisabled
        contextMenuItemFlag[remoteId + "DisableVideo"] = config.remoteVideoDisabled
        downStream.update(config)
    }

    private fun clearContextMenuItemFlag(id: String?) {
        val iterator = contextMenuItemFlag.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key.contains(id!!)) {
                iterator.remove()
            }
        }
    }
}
