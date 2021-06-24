package com.trx.consumer.screens.groupplayer

import android.content.Context
import android.media.projection.MediaProjection
import android.os.Handler
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
import fm.liveswitch.ChannelClaim
import fm.liveswitch.Client
import fm.liveswitch.ClientState.Registered
import fm.liveswitch.ClientState.Registering
import fm.liveswitch.ClientState.Unregistered
import fm.liveswitch.ClientState.Unregistering
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
import fm.liveswitch.MediaSourceState
import fm.liveswitch.PathUtility
import fm.liveswitch.PeerConnection
import fm.liveswitch.PeerConnectionOffer
import fm.liveswitch.Promise
import fm.liveswitch.SfuDownstreamConnection
import fm.liveswitch.SfuUpstreamConnection
import fm.liveswitch.SimulcastMode
import fm.liveswitch.StreamDirection
import fm.liveswitch.Token
import fm.liveswitch.VideoLayout
import fm.liveswitch.VideoStream
import fm.liveswitch.android.Camera2Source
import fm.liveswitch.android.LayoutManager
import fm.liveswitch.openh264.Utility
import java.util.ArrayList

class GroupPlayerHandler(val context: Context) {

    // TODO: Replace with Coroutines
    private var handler: Handler = Handler(context.mainLooper)
    private var channel: Channel? = null

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

    // Url for FM demo
    private val gatewayUrl = "https://demo.liveswitch.fm:8443/sync"

    // Generic applicationId for FM demo
    var live: LiveResponseModel = LiveResponseModel.test()
        set(value) {
            userID = value.sessionCustomerUid
            field = value
        }

    private val applicationId = "my-app-id"

    private var userID: String = Guid.newGuid().toString().replace("-".toRegex(), "")

    private val deviceID: String = Guid.newGuid().toString().replace("-".toRegex(), "")

    private var userName: String = "Testing"
        set(value) {
            field = if (value.isNotEmpty()) value else field
        }

    var channelId: String = "846812"

    //endregion

    // Track whether the user has decided to leave (unregister)
    // If they have not and the client gets into the Disconnected state then we attempt to reregister (reconnect) automatically.
    private var unRegistering = false
    private var reRegisterBackoff = 200
    private val maxRegisterBackoff = 60000

    private var isModeMcu: Boolean = false

    private lateinit var mcuViewId: String

    private var textListener: OnReceivedTextListener? = null
    private var usingFrontVideoDevice = true
    private var audioOnly: Boolean = false
    private var receiveOnly: Boolean = false
    var enableSimulcast = false

    //  TODO: Marked for removal. Used for screen sharing
    private var enableScreenShare = false

    val enableH264: Boolean
        get() = Utility.isSupported()

    //  TODO: Marked for removal. Used for screen projection.
    var mediaProjection: MediaProjection? = null

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

    fun startTRXLocalMedia(activity: GroupPlayerActivity): Future<Any> {
        val promise = Promise<Any>()

        // Set up the layout manager.
        layoutManager = LayoutManager(activity.container)
        activity.runOnUiThread {
            if (receiveOnly) {
                promise.resolve(null)
            } else {
                // Create an echo cancellation context.
                aecContext = AecContext()

                // Set up the local media.
                localMedia = LocalCameraMedia(
                    context,
                    enableH264,
                    false,
                    audioOnly,
                    aecContext,
                    enableSimulcast
                )

                localMedia?.view?.let { localView ->
                    localView.contentDescription = "localView"
                    layoutManager?.localView = localView
                }

                // Change input source to front camera .
                (localMedia?.videoSource as? Camera2Source)?.let { input ->
                    input.frontInput?.let { sourceInput ->
                        localMedia?.changeVideoSourceInput(sourceInput)
                    }
                }

                // Start the local media.
                localMedia?.let { safeLocalMedia ->
                    safeLocalMedia.start().then({
                        it.videoSource.start()
                        promise.resolve(null)
                    }) { e ->
                        promise.reject(e)
                    }
                }
            }
        }
        return promise
    }

    fun joinAsyncLive(): Future<Array<Channel>>? {
        unRegistering = false

        // Hardcoded
        client = Client(kFMGatewayUrl, kFMApplicationIdProd, live.sessionCustomerUid, deviceID)
            .apply {
                userAlias = live.participantName
                addOnStateChange { safeClient ->
                    LogManager.log("Client state is: ${safeClient.state.name} ")
                    if (safeClient.state == Unregistered && !unRegistering) {
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

        return client?.register(live.accessToken)?.then({ channels ->
            onClientRegistered(channels)
        }) { e ->
            LogManager.log("Failed to register with Gateway. ${e.message}")
        }
    }

    //endregion

    private var dataChannelsMessageTimer: ManagedTimer? = null

    // Used when opening Mcu connection and PeerAnswerConnection.
    private fun addRemoteViewOnUiThread(remoteMedia: RemoteMedia) {
        layoutManager?.let { safeLayoutManager ->
            handler.post {
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
            handler.post {
                safeLayoutManager.removeRemoteView(remoteMedia.id)
                remoteMedia.destroy()
            }
        }
    }

    // Used in onClientRegistered
    private fun layoutOnUiThread() {
        layoutManager?.let { safeLayoutManager ->
            handler.post {
                safeLayoutManager.layout()
            }
        }
    }

    fun startLocalMedia(activity: GroupPlayerActivity): Future<Any> {
        val promise = Promise<Any>()
        if (enableH264) {
            val downloadPath = context.filesDir.path
            Utility.downloadOpenH264(downloadPath).waitForResult()
            System.load(PathUtility.combinePaths(downloadPath, Utility.getLoadLibraryName()))
        }

        // Set up the layout manager.
        layoutManager = LayoutManager(activity.container)
        activity.runOnUiThread {
            if (receiveOnly) {
                promise.resolve(null)
            } else {
                // Create an echo cancellation context.
                aecContext = AecContext()

                // Set up the local media.
                localMedia = LocalCameraMedia(
                    context,
                    enableH264,
                    false,
                    audioOnly,
                    aecContext,
                    enableSimulcast
                )

                val localView = localMedia?.view
                if (localView != null) {
                    localView.contentDescription = "localView"
                    // livePlayerActivity?.registerLocalContextMenu(
                    //     localView,
                    //     localMedia?.videoEncodings
                    // )
                }
                layoutManager?.localView = localView

                // Start the local media.

                val input = localMedia?.videoSource as? Camera2Source
                input?.frontInput?.let {
                    localMedia?.changeVideoSourceInput(it)
                }

                localMedia?.start()?.then(
                    {
                        it.videoSource.start()
                        promise.resolve(null)
                    }
                ) { e -> promise.reject(e) }
            }
        }
        return promise
    }

    fun stopLocalMedia(): Future<Any> {
        val promise: Promise<Any> = Promise()
        groupPlayerActivity = null
        clearContextMenuItemFlag("localView")
        if (localMedia != null) {
            promise.resolve(null)
        } else {
            localMedia?.stop()?.then({ // Tear down the layout manager.
                if (layoutManager != null) {
                    layoutManager!!.removeRemoteViews()
                    layoutManager!!.unsetLocalView()
                    layoutManager = null
                }

                // Tear down the local media.
                if (localMedia != null) {
                    localMedia?.destroy() // localMedia.destroy() will also destroy aecContext.
                    // localMedia = null
                }
                promise.resolve(null)
            }) { e -> promise.reject(e) }
        }
        return promise
    }

    // Generate a register token.
    // WARNING: do NOT do this here!
    // Tokens should be generated by a secure server that
    // has authenticated your user identity and is authorized
    // to allow you to register with the LiveSwitch server.
    private fun generateToken(claims: Array<ChannelClaim>): String? {
        return Token.generateClientRegisterToken(
            applicationId,
            client!!.userId,
            client!!.deviceId,
            client!!.id,
            null,
            claims,
            "--replaceThisWithYourOwnSharedSecret--"
        )
    }

    // TODO: Remove when no longer testing hard coded FM demo values. 
    fun joinAsync(): Future<Array<Channel>>? {
        unRegistering = false

        // Create a client to manage the channel.
        client = Client(gatewayUrl, applicationId, userID, deviceID)
        val claims = arrayOf(ChannelClaim(channelId))
        client?.tag = if (isModeMcu) "Mcu" else "Sfu"
        client?.userAlias = userName
        client?.addOnStateChange { client ->
            if (client.state == Registering) {
                Log.debug("client is registering")
            } else if (client.state == Registered) {
                Log.debug("client is registered")
            } else if (client.state == Unregistering) {
                Log.debug("client is unregistering")
            } else if (client.state == Unregistered) {
                Log.debug("client is unregistered")

                // Client has failed for some reason:
                // We do not need to `c.closeAll()` as the client handled this for us as part of unregistering.
                if (!unRegistering) {

                    // Back off our reregister attempts as they continue to fail to avoid runaway process.
                    ManagedThread.sleep(reRegisterBackoff)
                    if (reRegisterBackoff < maxRegisterBackoff) {
                        reRegisterBackoff += reRegisterBackoff
                    }

                    // ReRegister
                    client.register(generateToken(claims)).then({ channels ->
                        reRegisterBackoff = 200 // reset for next time
                        onClientRegistered(channels)
                    }
                    ) { e -> Log.error("Failed to reregister with Gateway.", e) }
                }
            }
        }
        return client?.register(generateToken(claims))
            ?.then({ channels -> onClientRegistered(channels) }
            ) { e -> Log.error("Failed to register with Gateway.", e) }
    }

    private fun onClientRegistered(channels: Array<Channel>) {
        channel = channels.firstOrNull()

        // Monitor the channel remote client changes.
        channel?.addOnRemoteClientJoin { remoteClientInfo ->
            Log.info("Remote client joined the channel (client ID: " + remoteClientInfo.id + ", device ID: " + remoteClientInfo.deviceId + ", user ID: " + remoteClientInfo.userId + ", tag: " + remoteClientInfo.tag + ").")
            val n =
                if (remoteClientInfo.userAlias != null) remoteClientInfo.userAlias else remoteClientInfo.userId
            textListener?.onPeerJoined(n)
        }
        channel?.addOnRemoteClientLeave { remoteClientInfo ->
            val n =
                if (remoteClientInfo.userAlias != null) remoteClientInfo.userAlias else remoteClientInfo.userId
            textListener?.onPeerLeft(n)
            Log.info("Remote client left the channel (client ID: " + remoteClientInfo.id + ", device ID: " + remoteClientInfo.deviceId + ", user ID: " + remoteClientInfo.userId + ", tag: " + remoteClientInfo.tag + ").")
        }

        // Monitor the channel remote upstream connection changes.
        channel?.addOnRemoteUpstreamConnectionOpen { remoteConnectionInfo ->
            Log.info("Remote client opened upstream connection (connection ID: " + remoteConnectionInfo.id + ", client ID: " + remoteConnectionInfo.clientId + ", device ID: " + remoteConnectionInfo.deviceId + ", user ID: " + remoteConnectionInfo.userId + ", tag: " + remoteConnectionInfo.tag + ").")
            if (!isModeMcu) {
                // Open downstream connection to receive the new upstream connection.
                openSfuDownstreamConnection(remoteConnectionInfo, null)
            }
        }
        channel?.addOnRemoteUpstreamConnectionClose { remoteConnectionInfo ->
            Log.info(
                "Remote client closed upstream connection (connection ID: " + remoteConnectionInfo.id + ", client ID: " + remoteConnectionInfo.clientId + ", device ID: " + remoteConnectionInfo.deviceId + ", user ID: " + remoteConnectionInfo.userId + ", tag: " + remoteConnectionInfo.tag + ")."
            )
        }

        // Monitor the channel peer connection offers.
        channel?.addOnPeerConnectionOffer { peerConnectionOffer -> // Accept the peer connection offer.
            openPeerAnswerConnection(peerConnectionOffer)
        }
        channel?.addOnMessage { clientInfo, message ->
            val n = if (clientInfo.userAlias != null) clientInfo.userAlias else clientInfo.userId
            textListener?.onReceivedText(n, message)
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
        textListener?.onClientRegistered()
    }

    fun leaveAsync(): Future<Any?>? {
        return if (client != null) {
            unRegistering = true

            // Unregister with the server.
            client!!.unregister().then {
                textListener?.onClientUnregistered()
                dataChannelConnected = false
            }.fail(IAction1 { e -> Log.debug("Failed to Unregister Client", e) })
        } else {
            null
        }
    }

    private fun openMcuConnection(tag: String?): McuConnection? {
        // Create remote media to manage incoming media.
        val remoteMedia = RemoteMedia(context, enableH264, false, audioOnly, aecContext)
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
            val remoteEncodings = connection?.info?.videoStream?.sendEncodings
            if (remoteEncodings != null && remoteEncodings.isNotEmpty()) {
                videoStream.remoteEncoding = remoteEncodings[0]
            }
        }
        mcuConnection = connection

        // Tag the connection (optional).
        if (tag != null) {
            connection?.tag = tag
        }

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
                if (mcuConnection != null && !receiveOnly && !audioOnly) {
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
        if (tag != null) {
            connection?.tag = tag
        }

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
        val remoteMedia = RemoteMedia(context, enableH264, false, audioOnly, aecContext)

        // Add the remote video view to the layout.
        handler.post {
            layoutManager?.addRemoteView(
                remoteMedia.id,
                remoteMedia.view
            )
        }

        remoteMedia.view?.let { remoteView ->
            remoteView.contentDescription = "remoteView_" + remoteMedia.id
            // livePlayerActivity?.registerRemoteContextMenu(
            //     remoteView,
            //     if (remoteConnectionInfo.hasVideo) remoteConnectionInfo.videoStream.sendEncodings else null
            // )
        }

        var videoStream: VideoStream? = null
        var audioStream: AudioStream? = null
        if (remoteConnectionInfo.hasAudio) {
            audioStream = AudioStream(localMedia, remoteMedia)
        }
        if (remoteConnectionInfo.hasVideo && !audioOnly) {
            videoStream = VideoStream(localMedia, remoteMedia)
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

                    handler.post {
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
        val remoteMedia = RemoteMedia(context, enableH264, false, disableRemoteVideo, aecContext)

        // Add the remote video view to the layout.
        addRemoteViewOnUiThread(remoteMedia)
        remoteMedia.view?.let { remoteView ->
            remoteView.contentDescription = "remoteView_" + remoteMedia.id
            // livePlayerActivity?.registerRemoteContextMenu(remoteView, null)
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
                        // Note: no need to close the connection as it's done for us.
                        // Note: do not offer a new answer here. Let the offerer
                        // reoffer and then we answer normally.
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

    fun useNextVideoDevice() {
        if (localMedia != null && localMedia?.videoSource != null) {
            localMedia?.changeVideoSourceInput(if (usingFrontVideoDevice) (localMedia?.videoSource as Camera2Source).backInput else (localMedia?.videoSource as Camera2Source).frontInput)
            usingFrontVideoDevice = !usingFrontVideoDevice
        }
    }

    fun pauseLocalVideo(): Future<Any> {
        if (!enableScreenShare && localMedia != null) {
            val videoSource = localMedia?.videoSource
            if (videoSource != null) {
                if (videoSource.state == MediaSourceState.Started) {
                    return videoSource.stop()
                }
            }
        }
        return Promise.resolveNow()
    }

    fun resumeLocalVideo(): Future<Any> {
        if (localMedia != null) {
            val videoSource = localMedia?.videoSource
            if (videoSource != null) {
                if (videoSource.state == MediaSourceState.Stopped) {
                    return videoSource.start()
                }
            }
        }
        return Promise.resolveNow()
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
                textListener?.onReceivedText(
                    "System",
                    "$connectionType connection connected with $streams"
                )
            }
            ConnectionState.Closing -> {
                textListener?.onReceivedText(
                    "System",
                    "$connectionType connection closing for $streams"
                )
            }
            ConnectionState.Failing -> {
                var eventString: String? = "$connectionType connection failing for $streams"
                if (conn.error != null) {
                    eventString += conn.error.description
                }
                textListener?.onReceivedText("System", eventString)
            }
            ConnectionState.Closed -> {
                textListener?.onReceivedText(
                    "System",
                    "$connectionType connection closed for $streams"
                )
            }
            ConnectionState.Failed -> {
                textListener?.onReceivedText(
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
                if (dataChannelReceiveArgs.dataString != null) {
                    textListener?.onReceivedText(
                        "System",
                        "Data channel connection established. Received test message fromserver: " + dataChannelReceiveArgs.dataString
                    )
                }
                dataChannelConnected = true
            }
        }
        dataChannel.addOnStateChange { channel ->
            if (channel.state == DataChannelState.Connected) {
                if (dataChannelsMessageTimer == null) {
                    dataChannelsMessageTimer = ManagedTimer(1000, sendMessageInDataChannels())
                    dataChannelsMessageTimer!!.start()
                }
            }
        }
        return dataChannel
    }

    fun changeSendEncodings(index: Int) {
        val encodings = localMedia?.videoEncodings
        if (encodings != null) {
            encodings[index].deactivated = !encodings[index].deactivated
            localMedia?.videoEncodings = encodings
        }
    }

    fun changeReceiveEncodings(id: String, index: Int) {
        val connection =
            sfuDownstreamConnections[id.replace("remoteView_", "").trim { it <= ' ' }]
        val encodings = connection!!.remoteConnectionInfo.videoStream.sendEncodings
        if (encodings != null && encodings.size > 1) {
            val config = connection.config
            config.remoteVideoEncoding = encodings[index]
            connection.update(config)
                .then { Log.debug("Updated video encoding to: " + encodings[index] + " for connection: " + connection) }
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

    interface OnReceivedTextListener {
        fun onReceivedText(name: String?, message: String?)
        fun onPeerJoined(name: String?)
        fun onPeerLeft(name: String?)
        fun onClientRegistered()
        fun onClientUnregistered()
    }
}
