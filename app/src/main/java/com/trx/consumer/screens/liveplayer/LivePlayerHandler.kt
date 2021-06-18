package com.trx.consumer.screens.liveplayer

import android.content.Context
import android.media.projection.MediaProjection
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
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
import fm.liveswitch.ClientInfo
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
import fm.liveswitch.LogLevel
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
import fm.liveswitch.android.LogProvider
import fm.liveswitch.openh264.Utility
import java.util.ArrayList

class LivePlayerHandler(val context: Context) {

    enum class Modes(val value: Int) {
        Mcu(1), Sfu(2), Peer(3);
    }

    private var handler: Handler? = null
    private var channel: Channel? = null
    private var mcuConnection: McuConnection? = null
    private var sfuUpstreamConnection: SfuUpstreamConnection? = null
    private var sfuDownstreamConnections: HashMap<String, SfuDownstreamConnection>? = null
    private var peerConnections: HashMap<String, PeerConnection>? = null
    private lateinit var localMedia: LocalMedia<View>
    private lateinit var aecContext: AecContext
    private var layoutManager: LayoutManager? = null
    private var videoLayout: VideoLayout? = null
    var livePlayerActivity: LivePlayerActivity? = null
    var contextMenuItemFlag: HashMap<String, Boolean> = HashMap()
    var remoteMediaMaps: HashMap<String, ManagedConnection> = HashMap()

    private val gatewayUrl = "https://demo.liveswitch.fm:8443/sync"

    // Track whether the user has decided to leave (unregister)
    // If they have not and the client gets into the Disconnected state then we attempt to reregister (reconnect) automatically.
    private var unRegistering = false
    private var reRegisterBackoff = 200
    private val maxRegisterBackoff = 60000

    private var mode: Modes? = null

    private val applicationId = "my-app-id"
    private var userName: String? = "Testing"
    private var channelId: String? = "100000"
    private val deviceID: String = Guid.newGuid().toString().replace("-".toRegex(), "")
    private val userID: String = Guid.newGuid().toString().replace("-".toRegex(), "")
    private var mcuViewId: String? = null

    private var client: Client? = null

    private var textListener: OnReceivedTextListener? = null
    private var usingFrontVideoDevice = true
    private var audioOnly = false
    private var receiveOnly = false
    private var enableSimulcast = true
    private var enableScreenShare = false
    var enableH264 = false

    private var dataChannelConnected = false
    var dataChannels = ArrayList<DataChannel>()
    private val dataChannelLock =
        Any() // synchronize data channel book-keeping (collection may be modified while trying to send messages in SendDataChannelMessage())

    //region LivePlayerActivity Function

    fun start(activity: LivePlayerActivity, container: RelativeLayout, live: LiveResponseModel) {
        LogManager.log("LivePlayerHandler - start")
        layoutManager = LayoutManager(container)

        startLocalMedia(activity)
            .then({ joinAsyncLive(live) }) { e ->
                Log.error("Could not start local media", e)
            }
    }

    fun joinAsyncLive(live: LiveResponseModel): Future<Array<Channel?>?>? {
        val token = live.accessToken
        val name = live.participantName
        val userID: String = live.sessionCustomerUid

        unRegistering = false

        client = Client(
            kFMGatewayUrl,
            kFMApplicationIdProd,
            userID,
            deviceID
        )

        client?.userAlias = "Test"

        client?.addOnStateChange { state ->
            when (state.state) {
                Registering, Registered, Unregistering -> LogManager.log(state.state.name)
                Unregistered -> {
                    LogManager.log(state.state.name)
                    if (!unRegistering) {
                        ManagedThread.sleep(reRegisterBackoff)
                        if (reRegisterBackoff < maxRegisterBackoff) {
                            reRegisterBackoff += reRegisterBackoff
                        }
                    }
                }
                else -> {}
            }
        }

        // TODO: Complete function body

        return null
    }

    //endregion

    var dataChannelsMessageTimer: ManagedTimer? = null

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun setChannelId(cid: String?) {
        channelId = cid
    }

    fun setMode(m: Modes?) {
        mode = m
    }

    fun getMode(): Modes? {
        return mode
    }

    fun setAudioOnly(audioOnly: Boolean) {
        this.audioOnly = audioOnly
    }

    fun setReceiveOnly(receiveOnly: Boolean) {
        this.receiveOnly = receiveOnly
    }

    fun setEnableSimulcast(simulcast: Boolean) {
        enableSimulcast = simulcast
    }

    fun getEnableSimulcast(): Boolean {
        return enableSimulcast
    }

    fun setEnableScreenShare(screenShare: Boolean) {
        enableScreenShare = screenShare
    }

    fun getIsScreenShareEnabled(): Boolean {
        return enableScreenShare
    }

    private var mediaProjection: MediaProjection? = null

    fun getMediaProjection(): MediaProjection? {
        return mediaProjection
    }

    fun setMediaProjection(mediaProjection: MediaProjection?) {
        this.mediaProjection = mediaProjection
    }

    companion object {

        fun doSomething() {
            Log.setLogLevel(LogLevel.Debug)
            Log.setProvider(LogProvider(LogLevel.Debug))
            License.getCurrent()
        }
    }

    private fun LivePlayerHandler(context: Context) {
        handler = Handler(context.mainLooper)
        audioOnly = false
        receiveOnly = false
        contextMenuItemFlag = HashMap()
        remoteMediaMaps = HashMap()
        sfuDownstreamConnections = HashMap()
        peerConnections = HashMap()
    }

    private fun addRemoteViewOnUiThread(remoteMedia: RemoteMedia) {
        if (layoutManager == null) return
        val r = Runnable {
            if (remoteMedia.getView() != null) {
                remoteMedia.getView().setContentDescription("remoteView_" + remoteMedia.getId())
            }
            layoutManager!!.addRemoteView(remoteMedia.getId(), remoteMedia.getView())
        }
        handler!!.post(r)
    }

    private fun removeRemoteViewOnUiThread(remoteMedia: RemoteMedia) {
        if (layoutManager == null) return
        clearContextMenuItemFlag(remoteMedia.getId())
        val r = Runnable {
            layoutManager!!.removeRemoteView(remoteMedia.getId())
            remoteMedia.destroy()
        }
        handler!!.post(r)
    }

    private fun layoutOnUiThread() {
        if (layoutManager == null) return
        val r = Runnable { layoutManager!!.layout() }
        handler!!.post(r)
    }

    fun startLocalMedia(activity: LivePlayerActivity): Future<fm.liveswitch.LocalMedia> {
        val promise = Promise<fm.liveswitch.LocalMedia>()
        enableH264 = Utility.isSupported()
        if (enableH264) {
            val downloadPath = context!!.filesDir.path
            Utility.downloadOpenH264(downloadPath).waitForResult()
            System.load(PathUtility.combinePaths(downloadPath, Utility.getLoadLibraryName()))
        }

        // Set up the layout manager.
        activity.runOnUiThread {
            layoutManager = LayoutManager(activity.container)
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

                val localView = localMedia.view
                if (localView != null) {
                    localView.contentDescription = "localView"
                    livePlayerActivity?.registerLocalContextMenu(
                        localView,
                        (localMedia as LocalCameraMedia).videoEncodings
                    )
                }
                layoutManager!!.localView = localView

                // Start the local media.

                val input = localMedia.videoSource as? Camera2Source
                input?.frontInput?.let {
                    localMedia.changeVideoSourceInput(it)
                }

                (localMedia as LocalCameraMedia).start().then(
                    { promise.resolve(null) }
                ) { e -> promise.reject(e) }
            }
        }
        return promise
    }

    fun stopLocalMedia(): Future<fm.liveswitch.LocalMedia> {
        val promise: Promise<fm.liveswitch.LocalMedia> = Promise()
        livePlayerActivity = null
        clearContextMenuItemFlag("localView")
        if (!this::localMedia.isInitialized) {
            promise.resolve(null)
        } else {
            localMedia.stop()?.then({ // Tear down the layout manager.
                if (layoutManager != null) {
                    layoutManager!!.removeRemoteViews()
                    layoutManager!!.unsetLocalView()
                    layoutManager = null
                }

                // Tear down the local media.
                if (this::localMedia.isInitialized) {
                    localMedia.destroy() // localMedia.destroy() will also destroy aecContext.
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

    fun joinAsync(): Future<Array<Channel?>?>? {
        unRegistering = false

        // Create a client to manage the channel.
        client = Client(gatewayUrl, applicationId, userID, deviceID)
        val claims = arrayOf(ChannelClaim(channelId))
        client!!.tag = Integer.toString(mode!!.value)
        client!!.userAlias = userName
        client!!.addOnStateChange { client ->
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
        return client!!.register(generateToken(claims))
            .then({ channels -> onClientRegistered(channels) }
            ) { e -> Log.error("Failed to register with Gateway.", e) }
    }

    private fun onClientRegistered(channels: Array<Channel>) {
        channel = channels[0]

        // Monitor the channel remote client changes.
        channel!!.addOnRemoteClientJoin { remoteClientInfo ->
            Log.info("Remote client joined the channel (client ID: " + remoteClientInfo.id + ", device ID: " + remoteClientInfo.deviceId + ", user ID: " + remoteClientInfo.userId + ", tag: " + remoteClientInfo.tag + ").")
            val n =
                if (remoteClientInfo.userAlias != null) remoteClientInfo.userAlias else remoteClientInfo.userId
            textListener?.onPeerJoined(n)
        }
        channel!!.addOnRemoteClientLeave { remoteClientInfo ->
            val n =
                if (remoteClientInfo.userAlias != null) remoteClientInfo.userAlias else remoteClientInfo.userId
            textListener?.onPeerLeft(n)
            Log.info("Remote client left the channel (client ID: " + remoteClientInfo.id + ", device ID: " + remoteClientInfo.deviceId + ", user ID: " + remoteClientInfo.userId + ", tag: " + remoteClientInfo.tag + ").")
        }

        // Monitor the channel remote upstream connection changes.
        channel!!.addOnRemoteUpstreamConnectionOpen { remoteConnectionInfo ->
            Log.info("Remote client opened upstream connection (connection ID: " + remoteConnectionInfo.id + ", client ID: " + remoteConnectionInfo.clientId + ", device ID: " + remoteConnectionInfo.deviceId + ", user ID: " + remoteConnectionInfo.userId + ", tag: " + remoteConnectionInfo.tag + ").")
            if (mode == Modes.Sfu) {
                // Open downstream connection to receive the new upstream connection.
                openSfuDownstreamConnection(remoteConnectionInfo, null)
            }
        }
        channel!!.addOnRemoteUpstreamConnectionClose { remoteConnectionInfo ->
            Log.info(
                "Remote client closed upstream connection (connection ID: " + remoteConnectionInfo.id + ", client ID: " + remoteConnectionInfo.clientId + ", device ID: " + remoteConnectionInfo.deviceId + ", user ID: " + remoteConnectionInfo.userId + ", tag: " + remoteConnectionInfo.tag + ")."
            )
        }

        // Monitor the channel peer connection offers.
        channel!!.addOnPeerConnectionOffer { peerConnectionOffer -> // Accept the peer connection offer.
            openPeerAnswerConnection(peerConnectionOffer, null)
        }
        channel!!.addOnMessage { clientInfo, message ->
            val n = if (clientInfo.userAlias != null) clientInfo.userAlias else clientInfo.userId
            textListener?.onReceivedText(n, message)
        }
        if (mode == Modes.Mcu) {

            // Monitor the channel video layout changes.
            channel!!.addOnMcuVideoLayout { vidLayout ->
                if (!receiveOnly) {
                    videoLayout = vidLayout
                    // Force a layout in case the local video preview needs to move.
                    layoutOnUiThread()
                }
            }

            // Open an MCU connection.
            openMcuConnection(null)
        } else if (mode == Modes.Sfu) {
            if (!receiveOnly) {
                // Open an upstream SFU connection.
                openSfuUpstreamConnection(null)
            }

            // Open a downstream SFU connection for each remote upstream connection.
            for (connectionInfo in channel!!.remoteUpstreamConnectionInfos) {
                openSfuDownstreamConnection(connectionInfo, null)
            }
        } else if (mode == Modes.Peer) {
            // Open a peer connection for each remote client.
            for (clientInfo in channel!!.remoteClientInfos) {
                openPeerOfferConnection(clientInfo, null)
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
        mcuViewId = remoteMedia.getId()

        // Add the remote video view to the layout.
        addRemoteViewOnUiThread(remoteMedia)
        val connection: McuConnection
        val dataChannel = prepareDataChannel()
        val dataStream = DataStream(dataChannel)
        synchronized(dataChannelLock) { dataChannels.add(dataChannel) }
        val audioStream = AudioStream(localMedia, remoteMedia)
        if (receiveOnly) {
            audioStream.localDirection = StreamDirection.ReceiveOnly
        }
        if (audioOnly) {
            connection = channel!!.createMcuConnection(audioStream, dataStream)
        } else {
            val videoStream = VideoStream(localMedia, remoteMedia)
            if (receiveOnly) {
                videoStream.localDirection = StreamDirection.ReceiveOnly
            } else if (enableSimulcast && !audioOnly) {
                videoStream.simulcastMode = SimulcastMode.RtpStreamId
            }
            connection = channel!!.createMcuConnection(audioStream, videoStream, dataStream)
            val remoteEncodings = connection.info.videoStream.sendEncodings
            if (remoteEncodings != null && remoteEncodings.size > 0) {
                videoStream.remoteEncoding = remoteEncodings[0]
            }
        }
        mcuConnection = connection

        // Tag the connection (optional).
        if (tag != null) {
            connection.tag = tag
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection.addOnStateChange { connection ->
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
        layoutManager!!.addOnLayout { layout ->
            if (mcuConnection != null && !receiveOnly && !audioOnly) {
                LayoutUtility.floatLocalPreview<View>(
                    layout,
                    videoLayout,
                    mcuConnection!!.id,
                    mcuViewId,
                    localMedia?.viewSink
                )
            }
        }

        // Open the connection.
        connection.open()
        return connection
    }

    private fun openSfuUpstreamConnection(tag: String?): SfuUpstreamConnection? {
        // Create the connection.
        val connection: SfuUpstreamConnection
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
        connection = channel!!.createSfuUpstreamConnection(audioStream, videoStream, dataStream)
        sfuUpstreamConnection = connection

        // Tag the connection (optional).
        if (tag != null) {
            connection.tag = tag
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection.addOnStateChange { connection ->
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
        connection.open()
        return connection
    }

    private fun openSfuDownstreamConnection(
        remoteConnectionInfo: ConnectionInfo,
        tag: String?
    ): SfuDownstreamConnection? {
        // Create remote media to manage incoming media.
        val remoteMedia = RemoteMedia(context, enableH264, false, audioOnly, aecContext)

        // Add the remote video view to the layout.
        addRemoteViewOnUiThread(remoteMedia)
        val remoteView: View = remoteMedia.getView()
        if (remoteView != null) {
            remoteView.contentDescription = "remoteView_" + remoteMedia.getId()
            livePlayerActivity?.registerRemoteContextMenu(
                remoteView,
                if (remoteConnectionInfo.hasVideo) remoteConnectionInfo.videoStream.sendEncodings else null
            )
        }
        val connection: SfuDownstreamConnection
        var dataChannel: DataChannel? = null
        var dataStream: DataStream? = null
        if (remoteConnectionInfo.hasData) {
            dataChannel = prepareDataChannel()
            dataStream = DataStream(dataChannel)
        }
        var videoStream: VideoStream? = null
        var audioStream: AudioStream? = null
        if (remoteConnectionInfo.hasAudio) {
            audioStream = AudioStream(null, remoteMedia)
        }
        if (remoteConnectionInfo.hasVideo && !audioOnly) {
            videoStream = VideoStream(null, remoteMedia)
            if (enableSimulcast) {
                val remoteEncodings = remoteConnectionInfo.videoStream.sendEncodings
                if (remoteEncodings != null && remoteEncodings.size > 0) {
                    videoStream!!.remoteEncoding = remoteEncodings[0]
                }
            }
        }
        connection = channel!!.createSfuDownstreamConnection(
            remoteConnectionInfo,
            audioStream,
            videoStream,
            dataStream
        )
        sfuDownstreamConnections!![remoteMedia.getId()] = connection
        remoteMediaMaps!![remoteMedia.getId()] = connection

        // Tag the connection (optional).
        if (tag != null) {
            connection.tag = tag
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection.addOnStateChange { connection ->
            Log.info(connection.id + ": SFU downstream connection state is " + connection.state.toString() + ".")

            // Cleanup if the connection closes or fails.
            if (connection.state == ConnectionState.Closing || connection.state == ConnectionState.Failing) {
                if (connection.remoteClosed) {
                    Log.info(connection.id + ": Media server closed the connection.")
                }
                removeRemoteViewOnUiThread(remoteMedia)
                sfuDownstreamConnections!!.remove(remoteMedia.getId())
                remoteMediaMaps!!.remove(remoteMedia.getId())
                logConnectionState(connection, "SFU Downstream")
            } else if (connection.state == ConnectionState.Failed) {
                // Note: no need to close the connection as it's done for us.
                openSfuDownstreamConnection(remoteConnectionInfo, tag)
                logConnectionState(connection, "SFU Downstream")
            } else if (connection.state == ConnectionState.Connected) {
                logConnectionState(connection, "SFU Downstream")
            }
        }

        // Open the connection.
        connection.open()
        return connection
    }

    fun openPeerOfferConnection(remoteClientInfo: ClientInfo?, tag: String?): PeerConnection? {
        // Create remote media to manage incoming media.
        val remoteMedia = RemoteMedia(context, enableH264, false, audioOnly, aecContext)

        // Add the remote video view to the layout.
        addRemoteViewOnUiThread(remoteMedia)
        val remoteView: View = remoteMedia.getView()
        if (remoteView != null) {
            remoteView.contentDescription = "remoteView_" + remoteMedia.getId()
            livePlayerActivity?.registerRemoteContextMenu(remoteView, null)
        }
        val connection: PeerConnection
        val audioStream: AudioStream = AudioStream(localMedia, remoteMedia)
        var videoStream: VideoStream? = null
        if (!audioOnly) {
            videoStream = VideoStream(localMedia, remoteMedia)
        }

        // Please note that DataStreams can also be added to Peer-to-peer connections.
        // Nevertheless, since peer connections do not connect to the media server, there may arise
        // incompatibilities with the peers that do not support DataStream (e.g. Microsoft Edge browser:
        // https://developer.microsoft.com/en-us/microsoft-edge/platform/status/rtcdatachannels/?filter=f3f0000bf&search=rtc&q=data%20channels).
        // For a solution around this issue and complete documentation visit:
        // https://help.frozenmountain.com/docs/liveswitch1/working-with-datachannels
        connection = channel!!.createPeerConnection(remoteClientInfo, audioStream, videoStream)
        peerConnections!![connection.id] = connection
        remoteMediaMaps!![remoteMedia.getId()] = connection

        // Tag the connection (optional).
        if (tag != null) {
            connection.tag = tag
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection.addOnStateChange { connection ->
            Log.info(connection.id + ": Peer connection state is " + connection.state.toString() + ".")

            // Cleanup if the connection closes or fails.
            if (connection.state == ConnectionState.Closing || connection.state == ConnectionState.Failing) {
                if (connection.remoteRejected) {
                    Log.info(connection.id + ": Remote peer rejected the offer.")
                } else if (connection.remoteClosed) {
                    Log.info(connection.id + ": Remote peer closed the connection.")
                }
                removeRemoteViewOnUiThread(remoteMedia)
                peerConnections!!.remove(connection.id)
                remoteMediaMaps!!.remove(remoteMedia.getId())
                logConnectionState(connection, "Peer")
            } else if (connection.state == ConnectionState.Failed) {
                // Note: no need to close the connection as it's done for us.
                openPeerOfferConnection(remoteClientInfo, tag)
                logConnectionState(connection, "Peer")
            } else if (connection.state == ConnectionState.Connected) {
                logConnectionState(connection, "Peer")
            }
        }

        // Open the connection (sends an offer to the remote peer).
        connection.open()
        return connection
    }

    private fun openPeerAnswerConnection(
        peerConnectionOffer: PeerConnectionOffer,
        tag: String?
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
        val remoteView: View = remoteMedia.getView()
        if (remoteView != null) {
            remoteView.contentDescription = "remoteView_" + remoteMedia.getId()
            livePlayerActivity?.registerRemoteContextMenu(remoteView, null)
        }
        val connection: PeerConnection
        var videoStream: VideoStream? = null
        var audioStream: AudioStream? = null
        if (peerConnectionOffer.hasAudio) {
            audioStream = AudioStream(localMedia, remoteMedia)
        }
        if (peerConnectionOffer.hasVideo) {
            videoStream = VideoStream(localMedia, remoteMedia)
            if (audioOnly) {
                videoStream.localDirection = StreamDirection.Inactive
            }
        }

        // Please note that DataStreams can also be added to Peer-to-peer connections.
        // Nevertheless, since peer connections do not connect to the media server, there may arise
        // incompatibilities with the peers that do not support DataStream (e.g. Microsoft Edge browser:
        // https://developer.microsoft.com/en-us/microsoft-edge/platform/status/rtcdatachannels/?filter=f3f0000bf&search=rtc&q=data%20channels).
        // For a solution around this issue and complete documentation visit:
        // https://help.frozenmountain.com/docs/liveswitch1/working-with-datachannels
        connection = channel!!.createPeerConnection(peerConnectionOffer, audioStream, videoStream)
        peerConnections!![connection.id] = connection
        remoteMediaMaps!![remoteMedia.getId()] = connection

        // Tag the connection (optional).
        if (tag != null) {
            connection.tag = tag
        }

        /*
        Embedded TURN servers are used by default.  For more information refer to:
        https://help.frozenmountain.com/docs/liveswitch/server/advanced-topics#TURNintheMediaServer
        */

        // Monitor the connection state changes.
        connection.addOnStateChange { connection ->
            Log.info(connection.id + ": Peer connection state is " + connection.state.toString() + ".")

            // Cleanup if the connection closes or fails.
            if (connection.state == ConnectionState.Closing || connection.state == ConnectionState.Failing) {
                if (connection.remoteClosed) {
                    Log.info(connection.id + ": Remote peer closed the connection.")
                }
                removeRemoteViewOnUiThread(remoteMedia)
                peerConnections!!.remove(connection.id)
                remoteMediaMaps!!.remove(remoteMedia.getId())
                logConnectionState(connection, "Peer")
            } else if (connection.state == ConnectionState.Failed) {
                // Note: no need to close the connection as it's done for us.
                // Note: do not offer a new answer here. Let the offerer reoffer and then we answer normally.
                logConnectionState(connection, "Peer")
            } else if (connection.state == ConnectionState.Connected) {
                logConnectionState(connection, "Peer")
            }
        }

        // Open the connection (sends an answer to the remote peer).
        connection.open()
        return connection
    }

/*    fun useNextVideoDevice() {
        if (localMedia != null && localMedia?.videoSource != null) {
            localMedia.changeVideoSourceInput(if (usingFrontVideoDevice) (localMedia.getVideoSource() as Camera2Source).backInput else (localMedia.getVideoSource() as Camera2Source).frontInput)
            usingFrontVideoDevice = !usingFrontVideoDevice
        }
    }*/

    fun pauseLocalVideo(): Future<Any> {
        if (!enableScreenShare && localMedia != null) {
            val videoSource = localMedia?.videoSource ?: null
            if (videoSource != null) {
                if (videoSource.state == MediaSourceState.Started) {
                    return videoSource.stop()
                }
            }
        }
        return Promise.resolveNow()
    }

    fun resumeLocalVideo(): Future<Any> {
        if (this::localMedia.isInitialized) {
            val videoSource = localMedia.videoSource
            if (videoSource != null) {
                if (videoSource.state == MediaSourceState.Stopped) {
                    return videoSource.start()
                }
            }
        }
        return Promise.resolveNow()
    }

    fun writeLine(message: String?) {
        if (channel != null) // If the registration has not happened then "channel" will be null. So we want to send messages only after registration.
            {
                channel!!.sendMessage(message)
            }
    }

    private fun logConnectionState(conn: ManagedConnection, connectionType: String) {
        var streams = ""
        var streamCount = 0
        if (conn.audioStream != null) {
            streamCount++
            streams = "audio"
        }
        if (conn.dataStream != null) {
            if (streams.length > 0) {
                streams += "/"
            }
            streamCount++
            streams += "data"
        }
        if (conn.videoStream != null) {
            if (streams.length > 0) {
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
        if (conn.state == ConnectionState.Connected) {
            textListener?.onReceivedText(
                "System",
                "$connectionType connection connected with $streams"
            )
        } else if (conn.state == ConnectionState.Closing) {
            textListener?.onReceivedText(
                "System",
                "$connectionType connection closing for $streams"
            )
        } else if (conn.state == ConnectionState.Failing) {
            var eventString: String? = "$connectionType connection failing for $streams"
            if (conn.error != null) {
                eventString += conn.error.description
            }
            textListener?.onReceivedText("System", eventString)
        } else if (conn.state == ConnectionState.Closed) {
            textListener?.onReceivedText(
                "System",
                "$connectionType connection closed for $streams"
            )
        } else if (conn.state == ConnectionState.Failed) {
            textListener?.onReceivedText(
                "System",
                "$connectionType connection failed for $streams"
            )
        }
    }

    private fun sendMessageInDataChannels(): IAction0? {
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
        dataChannel.addOnStateChange { dataChannel ->
            if (dataChannel.state == DataChannelState.Connected) {
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
            sfuDownstreamConnections!![id.replace("remoteView_", "").trim { it <= ' ' }]
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
        for (peerConnection in peerConnections!!.values) {
            config = peerConnection.config
            config.localAudioMuted = !config.localAudioMuted
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag!!["MuteAudio"] = config.localAudioMuted
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
        for (peerConnection in peerConnections!!.values) {
            config = peerConnection.config
            config.localVideoMuted = !config.localVideoMuted
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag!!["MuteVideo"] = config.localVideoMuted
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
        for (peerConnection in peerConnections!!.values) {
            config = peerConnection.config
            config.localAudioDisabled = !config.localAudioDisabled
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag!!["DisableAudio"] = config.localAudioDisabled
        }
    }

    fun toggleRemoteDisableAudio(remoteId: String) {
        val id = remoteId.replace("remoteView_", "")
        val downStream = remoteMediaMaps!![id]
        val config = downStream!!.config
        config.remoteAudioDisabled = !config.remoteAudioDisabled
        contextMenuItemFlag!![remoteId + "DisableAudio"] = config.remoteAudioDisabled
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
        for (peerConnection in peerConnections!!.values) {
            config = peerConnection.config
            config.localVideoDisabled = !config.localVideoDisabled
            peerConnection.update(config)
        }
        if (config != null) {
            contextMenuItemFlag!!["DisableVideo"] = config.localVideoDisabled
        }
    }

    fun toggleRemoteDisableVideo(remoteId: String) {
        val id = remoteId.replace("remoteView_", "")
        val downStream = remoteMediaMaps!![id]
        val config = downStream!!.config
        config.remoteVideoDisabled = !config.remoteVideoDisabled
        contextMenuItemFlag!![remoteId + "DisableVideo"] = config.remoteVideoDisabled
        downStream.update(config)
    }

    fun clearContextMenuItemFlag(id: String?) {
        val iterator = contextMenuItemFlag!!.keys.iterator()
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