package com.trx.consumer.screens.privateplayer

interface PrivatePlayerListener {
    fun doReceiveMessage(clientId: String, message: String)
    fun doReportError(message: String)
}
