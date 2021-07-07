package com.trx.consumer.screens.privateplayer

import android.content.Context
import android.view.View
import com.trx.consumer.frozenmountain.LiveSwitchWithTrainerHandlerBase
import com.trx.consumer.frozenmountain.LocalMedia

class PrivatePlayerHandler(override val context: Context) : LiveSwitchWithTrainerHandlerBase(context) {
    override fun opensNonTrainerDownstreamConnections(): Boolean {
        return false
    }
}
