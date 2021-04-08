package com.trx.consumer.models.common

class VirtualWorkoutModel(
    var imageUrl: String = "",
    var title: String = "",
    var subtitle: String = "",
    var duration: String = "",
    var amount: String = "",
    var remaining: String = "",
    var date: String = "",
    var time: String = "",
    var summary: String = "",
    var equipment: String = "",
    var trainerModel: TrainerModel = TrainerModel(),
    var status: VirtualBookingViewState = VirtualBookingViewState.BOOK
) {

    val subtitle1: String
        get() = "$duration    $amount    $remaining"

    val topSubtitle: String
        get() = "$date, $time"

    companion object {

        fun test(status: VirtualBookingViewState): VirtualWorkoutModel {
            return VirtualWorkoutModel(
                status = status,
                title = "Virtual Training Session with Jamie",
                subtitle = when (status) {
                    VirtualBookingViewState.BOOK -> "SEE JAMIE'S AVAILABILITY"
                    VirtualBookingViewState.SOON -> "STARTING IN 10 MIN"
                    VirtualBookingViewState.FUTURE -> "TODAY, 11 AM"
                }
            )
        }

        fun test1(): VirtualWorkoutModel {
            return VirtualWorkoutModel(
                imageUrl = "",
                title = "Small Group Training",
                duration = "60 MIN",
                amount = "$19.99",
                remaining = "3 Remaining",
                date = "TODAy",
                time = "11 AM"
            )
        }

        fun testList(count: Int): List<VirtualWorkoutModel> {
            return (0 until count).map { test(VirtualBookingViewState.SOON) }
        }

        fun testListToday(count: Int): List<VirtualWorkoutModel> {
            return (0 until count).map { test(VirtualBookingViewState.BOOK) }
        }

        fun testListVariety(): List<VirtualWorkoutModel> {
            return listOf(
                test(VirtualBookingViewState.BOOK),
                test(VirtualBookingViewState.SOON),
                test(VirtualBookingViewState.FUTURE),
            )
        }
    }
}
