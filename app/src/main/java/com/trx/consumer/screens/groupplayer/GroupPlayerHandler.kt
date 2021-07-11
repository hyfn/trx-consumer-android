package com.trx.consumer.screens.groupplayer

import android.content.Context
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.frozenmountain.LiveSwitchWithTrainerHandlerBase
import com.trx.consumer.frozenmountain.Participant
import com.trx.consumer.frozenmountain.RemoteMedia
import fm.liveswitch.ConnectionInfo
import fm.liveswitch.SfuDownstreamConnection

class GroupPlayerHandler(context: Context) : LiveSwitchWithTrainerHandlerBase(context) {
    var eventParticipantAdded = CommonLiveEvent<Participant>()

    override fun opensNonTrainerDownstreamConnections(): Boolean {
        return false
    }

    override fun sfuConnectionCreated(
        remoteConnInfo: ConnectionInfo,
        remoteMedia: RemoteMedia,
        connection: SfuDownstreamConnection
    ) {
        super.sfuConnectionCreated(remoteConnInfo, remoteMedia, connection)
        val participant = Participant(remoteConnInfo.userId, remoteConnInfo.userAlias, remoteMedia, connection, remoteConnInfo)
        eventParticipantAdded.postValue(participant)
    }
}
