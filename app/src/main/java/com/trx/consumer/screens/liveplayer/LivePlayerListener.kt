package com.trx.consumer.screens.liveplayer

interface LivePlayerListener {
    fun doReceiveMessage(clientId: String, message: String)
    fun doReportError(message: String)
}
