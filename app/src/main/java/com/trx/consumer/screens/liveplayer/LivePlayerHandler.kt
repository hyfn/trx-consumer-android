package com.trx.consumer.screens.liveplayer

import android.content.Context
import android.media.projection.MediaProjection
import android.os.Handler
import android.view.View
import com.trx.consumer.BuildConfig.kFMApplicationIdProd
import com.trx.consumer.BuildConfig.kFMGatewayUrl
import com.trx.consumer.frozenmountain.*
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

class LivePlayerHandler(context: Context): LiveSwitchWithTrainerHandlerBase(context) {
    override fun opensNonTrainerDownstreamConnections(): Boolean {
        return false
    }
}
