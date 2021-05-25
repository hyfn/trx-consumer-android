package com.trx.consumer.models.common

import android.os.Parcelable
import androidx.annotation.StringRes
import com.trx.consumer.R
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class BookingAlertModel(
    var card: CardModel = CardModel(),
    var workout: WorkoutModel
) : Parcelable {

    val buttonTitle: String
        get() = "book now ${workout.amount}"

    @get:StringRes
    val title: Int
        get() =
            when (workout.state) {
                BookingState.BOOKED -> {
                    //  TODO: Change to this logic once cellViewStatus implemented
                    // if(workout.cellViewStatus == WorkoutCellViewState.SOON) {
                    //     R.string.booking_alert_join_title
                    // } else {
                    //     R.string.booking_alert_cancel_title
                    // }
                    R.string.booking_alert_join_title
                }
                else -> R.string.booking_alert_book_title
            }

    companion object {

        fun parse(jsonObject: JSONObject): BookingAlertModel =
            BookingAlertModel(
                card = jsonObject.optJSONObject("card")?.let {
                    CardModel.parse(it)
                } ?: CardModel(),
                workout = jsonObject.optJSONObject("workout")?.let {
                    WorkoutModel.parse(it)
                } ?: WorkoutModel()
            )

        fun test(): BookingAlertModel =
            BookingAlertModel(
                card = CardModel.test(),
                workout = WorkoutModel.test()
            )

        fun testList(count: Int): List<BookingAlertModel> {
            return (0 until count).map { test() }
        }
    }
}
