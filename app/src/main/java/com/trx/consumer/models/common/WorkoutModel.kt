package com.trx.consumer.models.common

import android.os.Parcelable
import android.text.format.DateUtils
import com.trx.consumer.extensions.format
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.time.Instant
import java.util.Date
import java.util.TimeZone

@Parcelize
class WorkoutModel(
    val summary: String = "",
    val imageUrl: String = "",
    val durationInMinutes: Int = 0,
    val maxParticipants: Int = 0,
    val title: String = "",
    val canceledAt: Int? = null,
    val startsAt: Long = 0,
    val priceInCents: Int = 0,
    val remaining: String = "",
    val trainer: TrainerModel = TrainerModel(),
    val state: BookingState = BookingState.BOOK,
    val mode: String = "",
    val identifier: String = "",
    val sessionId: String = "",
    val cancelId: String? = null,
    val bookingTimestamp: Double = 0.0,
    val video: VideoModel = VideoModel()
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

        fun parse(jsonObject: JSONObject): WorkoutModel {
            val canceledAt = jsonObject.optInt("canceledAt")
            val trainerKey = jsonObject.optString("sessionOwnerUid")
            val trainer = jsonObject.optJSONObject("trainer")?.let {
                TrainerModel.parse(it)
            } ?: TrainerModel()

            return WorkoutModel(
                summary = jsonObject.optString("description"),
                imageUrl = jsonObject.optString("heroImage"),
                durationInMinutes = jsonObject.optInt("durationInMinutes"),
                maxParticipants = jsonObject.optInt("maxParticipants"),
                title = jsonObject.optString("name"),
                canceledAt = if (canceledAt == 0) null else canceledAt,
                startsAt = jsonObject.optLong("startsAt"),
                priceInCents = jsonObject.optInt("priceInCents"),
                mode = jsonObject.optString("mode"),
                identifier = jsonObject.optString("key"),
                sessionId = jsonObject.optString("sessionKey"),
                trainer = trainer,
            ).apply { trainer.key = trainerKey }
        }

        fun test(): WorkoutModel {
            return WorkoutModel(
                identifier = "123",
                durationInMinutes = 60,
                imageUrl = "",
                title = "TRX Strength & Conditioning",
                startsAt = 1615917600000,
                state = BookingState.VIEW,
                trainer = TrainerModel.test(),
                video = VideoModel.test()
            )
        }

        fun testList(count: Int): List<WorkoutModel> {
            return (0 until count).map { test() }
        }
    }
}
