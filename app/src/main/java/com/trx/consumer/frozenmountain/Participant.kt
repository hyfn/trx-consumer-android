package com.trx.consumer.frozenmountain

import fm.liveswitch.ConnectionInfo
import fm.liveswitch.SfuDownstreamConnection

class Participant(userId: String, userName: String, remoteMedia: RemoteMedia, connection: SfuDownstreamConnection, connectionInfo: ConnectionInfo) {
    var userId: String = ""
    var userName: String = ""
    var remoteMedia: RemoteMedia
    var connection: SfuDownstreamConnection
    var connectionInfo: ConnectionInfo

    init {
        this.userId = userId
        this.userName = userName
        this.remoteMedia = remoteMedia
        this.connection = connection
        this.connectionInfo = connectionInfo
    }

}