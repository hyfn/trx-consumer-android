package com.trx.consumer.screens.groupplayer

import android.content.Context
import com.trx.consumer.frozenmountain.LiveSwitchWithTrainerHandlerBase

class GroupPlayerHandler(context: Context) : LiveSwitchWithTrainerHandlerBase(context) {
    override fun opensNonTrainerDownstreamConnections(): Boolean {
        return true
    }
}
