package com.trx.consumer.screens.privateplayer

import android.content.Context
import com.trx.consumer.frozenmountain.LiveSwitchWithTrainerHandlerBase

class PrivatePlayerHandler(context: Context) : LiveSwitchWithTrainerHandlerBase(context) {
    override fun opensNonTrainerDownstreamConnections(): Boolean {
        return false
    }
}
