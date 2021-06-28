package com.trx.consumer.models.responses

import android.os.Parcelable
import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.ParticipantModel
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class LiveResponseModel(
    var accessToken: String = "",
    var participants: List<ParticipantModel> = listOf(),
    var playerType: String = "",
    var sessionCustomerUid: String = ""
) : Parcelable {

    val isValidType: Boolean
        get() = (playerType == "BROADCAST")

    val participantName: String
        get() = participants.firstOrNull { it.uid == sessionCustomerUid }?.firstName ?: ""

    companion object {

        fun parse(json: String): LiveResponseModel {
            val jsonObject = JSONObject(json).getJSONObject("data")
            return LiveResponseModel(
                accessToken = jsonObject.optString("accessToken"),
                participants = jsonObject.optJSONArray("data").map {
                    ParticipantModel.parse(it)
                },
                playerType = jsonObject.optString("playerType"),
                sessionCustomerUid = jsonObject.optString("sessionCustomerUid")
            )
        }

        fun test(): LiveResponseModel =
            LiveResponseModel(
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                    "eyJpYXQiOjE2MjA4NTA1OTEsImV4cCI6MTYyMDg1NDE5MSwiaXNzIjoiaHR0cHM6Ly92aXJ0dW" +
                    "FsLXRyYWluaW5nLXByLTIxMC5oZXJva3VhcHAuY29tIiwibmJmIjoxNjIwODUwNTkxLCJ0eXBl" +
                    "Ijoiam9pbiIsImFwcGxpY2F0aW9uSWQiOiJlNTEyOWNhZS1hZTc1LTQzNTEtOWMyOS00YTAxMm" +
                    "MzYzU0NzEiLCJ1c2VySWQiOiI0ZWYwMDlhNTE1MGM0NjE1ODc2NWY2NDk3YWNjODE1YiIsImNo" +
                    "YW5uZWxzIjpbeyJpZCI6Ii1NXzFzU0hoUG5JMmtPZ2wtNk5XIn1dfQ.O5tfyV_dzosJmRWb9Ru" +
                    "DvUF2lT3OWMriCavvubSM5nM"
            )
    }
}
