package com.trx.consumer.screens.groupplayer

import android.content.Context
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.frozenmountain.LiveSwitchWithTrainerHandlerBase
import com.trx.consumer.frozenmountain.Participant
import com.trx.consumer.frozenmountain.RemoteMedia
import com.trx.consumer.managers.LogManager.log
import fm.liveswitch.ConnectionInfo
import fm.liveswitch.SfuDownstreamConnection
import java.util.logging.LogManager

class GroupPlayerHandler(context: Context) : LiveSwitchWithTrainerHandlerBase(context) {
    var participants = ArrayList<Participant>()
    var eventParticipantChanged = CommonLiveEvent<Boolean>()

    override fun opensNonTrainerDownstreamConnections(): Boolean {
        return false
    }

    override fun sfuConnectionCreated(
        remoteConnInfo: ConnectionInfo,
        remoteMedia: RemoteMedia,
        connection: SfuDownstreamConnection
    ) {
        super.sfuConnectionCreated(remoteConnInfo, remoteMedia, connection)
        val participant = Participant(
            remoteConnInfo.userId,
            remoteConnInfo.userAlias,
            remoteMedia,
            connection,
            remoteConnInfo
        )

        participants.add(participant)
        eventParticipantChanged.postValue(true)
    }

    override fun closeSfuDownStreamConnection(remoteConnectionInfo: ConnectionInfo) {
        super.closeSfuDownStreamConnection(remoteConnectionInfo)
        var participant = participants.find { x -> x.connectionInfo.id == remoteConnectionInfo.id }
        if(participant != null) {
            participant.connection.close()
            participants.remove(participant)
            eventParticipantChanged.postValue(false)
        }
    }

    fun removeParticipants()
    {
        for(participant in participants)
        {
            participant.connection.close()
        }
        participants = ArrayList<Participant>()
    }

    fun getParticipantByPosition(position: Int): Participant?
    {
        if(participants.count() > position)
            return participants[position]

        return null;
    }

}
