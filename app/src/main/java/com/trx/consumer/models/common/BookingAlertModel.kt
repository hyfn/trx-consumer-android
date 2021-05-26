package com.trx.consumer.models.common

import android.os.Parcelable
import androidx.annotation.StringRes
import com.trx.consumer.R
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.models.states.WorkoutViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class BookingAlertModel(
    var card: CardModel? = null,
    var workout: WorkoutModel
) : Parcelable {

    val buttonTitle: String
        get() = "book now (${workout.amount})"

    @get:StringRes
    val title: Int
        get() =
            when (workout.state) {
                BookingState.BOOKED -> {
                    if (workout.cellViewStatus == WorkoutCellViewState.SOON) {
                        R.string.booking_alert_join_title
                    } else {
                        R.string.booking_alert_cancel_title
                    }
                }
                else -> R.string.booking_alert_book_title
            }

    companion object {

        fun parse(jsonObject: JSONObject): BookingAlertModel =
            BookingAlertModel(
                card = jsonObject.optJSONObject("card")?.let { CardModel.parse(it) },
                workout = jsonObject.optJSONObject("workout")?.let {
                    WorkoutModel.parse(it)
                } ?: WorkoutModel()
            )

        fun test(): BookingAlertModel =
            BookingAlertModel(
                card = CardModel.test(),
                workout = WorkoutModel.test()
            )

        fun testLiveBook(): BookingAlertModel =
            test().apply {
                workout.state = BookingState.BOOKED
                workout.mode = WorkoutViewState.LARGE_GROUP_MODE
                workout.cancelId = "test"
            }

        fun testNoCardLiveBook() {
            testLiveBook().apply {
                card = null
            }
        }

        fun testVirtualBook(): BookingAlertModel =
            test().apply {
                workout.state = BookingState.BOOKED
                workout.mode = WorkoutViewState.ONE_ON_ONE_MODE
                workout.cancelId = "test"
            }

        fun testNoCardVirutalBook(): BookingAlertModel =
            testVirtualBook().apply {
                card = null
            }

        fun testList(count: Int): List<BookingAlertModel> {
            return (0 until count).map { test() }
        }
    }
}
