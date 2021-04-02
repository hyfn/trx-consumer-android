package com.trx.consumer.models.common

import android.os.Parcelable
import android.text.format.DateUtils
import com.trx.consumer.extensions.format
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.Date
import java.util.TimeZone

@Parcelize
class LiveWorkoutModel(
    var identifier: String = "",
    var durationInMinutes: Int = 0,
    var imageUrl: String = "",
    var startsAt: Long = 0,
    var status: BookingViewState = BookingViewState.VIEW,
    var title: String = "",
    var trainer: TrainerModel = TrainerModel(),
    var summary: String = "",
    var equipment: String = ""
) : Parcelable {

    val date: Date
        get() = Date.from(Instant.ofEpochMilli(startsAt.toLong()))

    val dateDisplay: String
        get() {
            val date = date
            return if (DateUtils.isToday(date.time)) {
                "Today"
            } else {
                date.format(format = "MMM d", zone = TimeZone.getDefault())
            }
        }

    val dateTime: String
        get() = "$dateDisplay, $time"

    val duration: String
        get() = if (durationInMinutes == 0) "" else "$durationInMinutes MIN"

    val subtitle: String
        get() = "$dateDisplay, $time   $duration"

    val time: String
        get() = date.format("h:mm a", zone = TimeZone.getDefault())

    companion object {

        fun test(): LiveWorkoutModel {
            return LiveWorkoutModel(
                identifier = "123",
                durationInMinutes = 60,
                imageUrl = "",
                title = "TRX Strength & Conditioning",
                startsAt = 1615917600000,
                status = BookingViewState.VIEW,
                trainer = TrainerModel.test()
            )
        }

        fun testList(count: Int): List<LiveWorkoutModel> {
            return (0 until count).map { test() }
        }
    }
}
