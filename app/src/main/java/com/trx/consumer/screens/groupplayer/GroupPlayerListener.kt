package com.trx.consumer.screens.groupplayer

interface GroupPlayerListener {
    fun doReceiveMessage(clientId: String, message: String)
    fun doReportError(message: String)
}
