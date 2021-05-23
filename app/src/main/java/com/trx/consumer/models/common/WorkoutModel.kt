package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.BuildConfig
import com.trx.consumer.extensions.elapsedMin
import com.trx.consumer.extensions.format
import com.trx.consumer.extensions.isToday
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
    var state: BookingState = BookingState.BOOK,
    val mode: String = "",
    var identifier: String = "",
    var sessionId: String = "",
    var cancelId: String? = null,
    val bookingTimestamp: Double = 0.0,
    val video: VideoModel = VideoModel()
) : Parcelable {

    val date: Date
        get() = Date.from(Instant.ofEpochMilli(startsAt))

    val dateDisplay: String
        get() {
            val date = date
            return if (date.isToday()) {
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

    val isCanceled: Boolean
        get() = canceledAt != null

    val workoutState: WorkoutViewState
        get() = WorkoutViewState.from(mode)

    // Updated workout model when it comes from the booking endpoint.
    val booking: WorkoutModel
        get() {
            state = BookingState.BOOKED
            cancelId = when (workoutState) {
                WorkoutViewState.LIVE -> identifier
                WorkoutViewState.VIRTUAL -> sessionId
                else -> cancelId
            }
            val tempIdentifier = sessionId
            sessionId = identifier
            identifier = tempIdentifier

            return this
        }

    val cellViewStatus: WorkoutCellViewState
        get() {
            if (workoutState == WorkoutViewState.VIRTUAL) {
                if (date.elapsedMin() < BuildConfig.kMinutesAfterCanJoin) {
                    return WorkoutCellViewState.SOON
                }
            }
            return WorkoutCellViewState.VIEW
        }


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
                imageUrl = "https://cf-images.us-east-1.prod.boltdns.net/v1/jit/6204326362001/9ad5d77c-99f7-4c65-8a2d-40ac2546fd01/main/1280x720/55s189ms/match/image.jpg",
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
