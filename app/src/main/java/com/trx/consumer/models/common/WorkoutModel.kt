package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.BuildConfig.kMinutesBeforeCanJoin
import com.trx.consumer.extensions.elapsedMin
import com.trx.consumer.extensions.format
import com.trx.consumer.extensions.isToday
import com.trx.consumer.models.responses.LiveResponseModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.models.states.BookingViewState
import com.trx.consumer.models.states.WorkoutViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.time.Instant
import java.util.Date
import java.util.TimeZone
import kotlin.math.abs

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
    var mode: String = "",
    var identifier: String = "",
    var sessionId: String = "",
    var cancelId: String? = null,
    val bookingTimestamp: Double = 0.0,
    var video: VideoModel = VideoModel(),
    var live: LiveResponseModel = LiveResponseModel()
) : Parcelable {

    val amount: String
        get() = "$${priceInCents / 100.00}"

    override fun equals(other: Any?): Boolean {
        return other === this || (other is WorkoutModel && other.identifier == identifier)
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

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

    private var currency: String = "usd"

    val workoutState: WorkoutViewState
        get() = WorkoutViewState.from(mode)

    val isCancelled: Boolean
        get() = canceledAt != null

    val booking: WorkoutModel
        get() = this.apply {
            state = BookingState.BOOKED
            when (workoutState) {
                WorkoutViewState.LIVE -> cancelId = identifier
                WorkoutViewState.VIRTUAL -> cancelId = sessionId
                else -> {
                }
            }
            sessionId = identifier.also { identifier = sessionId }
        }

    val cellViewStatus: WorkoutCellViewState
        get() {
            if (workoutState == WorkoutViewState.VIRTUAL) {
                val minLeft = date.elapsedMin()
                if (minLeft < 0 && abs(minLeft) < kMinutesBeforeCanJoin) {
                    return WorkoutCellViewState.SOON
                }
            }
            return WorkoutCellViewState.VIEW
        }

    val bookViewStatus: BookingViewState
        get() = when (state) {
            BookingState.BOOKED -> {
                val minLeft = date.elapsedMin()
                if (minLeft < 0 && abs(minLeft) < kMinutesBeforeCanJoin) BookingViewState.JOIN
                else BookingViewState.CANCEL
            }
            BookingState.DISABLED -> BookingViewState.VIDEO
            else -> BookingViewState.BOOK
        }

    fun paramsSessionConfirm(invoiceId: String): HashMap<String, Any> =
        hashMapOf(
            "eventKey" to identifier,
            "invoiceId" to invoiceId
        )

    val paramsSessionIntent: HashMap<String, Any>
        get() =
            hashMapOf(
                "currency" to currency,
                "eventKey" to identifier
            )

    val paramsProgramIntent: HashMap<String, Any>
        get() =
            hashMapOf(
                "currency" to currency,
                "eventKey" to identifier,
                "bookings" to listOf(bookingTimestamp)
            )

    fun paramsProgramConfirm(invoiceId: String): HashMap<String, Any> =
        hashMapOf(
            "eventKey" to identifier,
            "invoiceId" to invoiceId,
            "bookings" to listOf(bookingTimestamp)
        )

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

        fun testVirtual(status: WorkoutCellViewState = WorkoutCellViewState.VIEW): WorkoutModel {
            val date = if (status == WorkoutCellViewState.SOON) {
                Date(System.currentTimeMillis() + 300000)
            } else {
                Date(1716619600000)
            }
            return WorkoutModel(
                identifier = "123",
                durationInMinutes = 60,
                imageUrl = "https://cf-images.us-east-1.prod.boltdns.net/v1/jit/6204326362001/9ad5d77c-99f7-4c65-8a2d-40ac2546fd01/main/1280x720/55s189ms/match/image.jpg",
                title = "Virtual Training Session with Jamie",
                startsAt = date.time,
                state = BookingState.VIEW,
                trainer = TrainerModel.test(),
                video = VideoModel.test(),
                mode = WorkoutViewState.ONE_ON_ONE_MODE
            )
        }

        fun testLive(): WorkoutModel {
            return WorkoutModel(
                identifier = "123",
                durationInMinutes = 60,
                imageUrl = "https://cf-images.us-east-1.prod.boltdns.net/v1/jit/6204326362001/9ad5d77c-99f7-4c65-8a2d-40ac2546fd01/main/1280x720/55s189ms/match/image.jpg",
                title = "TRX Strength & Conditioning",
                startsAt = 1615917600000,
                live = LiveResponseModel.test(),
                state = BookingState.VIEW,
                trainer = TrainerModel.test(),
                video = VideoModel.test(),
                mode = WorkoutViewState.LARGE_GROUP_MODE,
                priceInCents = 1999,
            )
        }

        fun testLiveSession(): WorkoutModel {
            return WorkoutModel(
                identifier = "-M_mCHodpDeZstx85Avz",
                durationInMinutes = 60,
                imageUrl = "https://digital.trxtraining.com/banner-images/class-detail-large-group.png",
                title = "Developer Session for Mobile",
                startsAt = 1749985200000,
                live = LiveResponseModel.test(),
                mode = WorkoutViewState.LARGE_GROUP_MODE,
                priceInCents = 1999,
            )
        }

        fun testListLive(count: Int): List<WorkoutModel> {
            return (0 until count).map { testLive() }
        }

        fun testListVirtual(count: Int, status: WorkoutCellViewState): List<WorkoutModel> {
            return (0 until count).map { testVirtual(status) }
        }
    }
}
