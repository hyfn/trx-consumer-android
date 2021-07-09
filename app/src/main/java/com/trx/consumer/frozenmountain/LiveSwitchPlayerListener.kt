package com.trx.consumer.frozenmountain

interface LiveSwitchPlayerListener {
    fun doReceiveMessage(clientId: String, message: String)
    fun doReportError(message: String)
}
