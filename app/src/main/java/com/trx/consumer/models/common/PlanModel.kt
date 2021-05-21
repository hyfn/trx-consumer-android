package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.screens.plans.list.PlansViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class PlanModel(
    var key: String = "",
    var title: String? = null,
    var description: String?,
    var cost: String?,
    var primaryState: PlansViewState = PlansViewState.OTHER,
    var startsAt: Int = 0
) : Parcelable {

    companion object {

        fun test(): PlanModel {
            return PlanModel(
                key = "",
                title = "Unlimited live classes",
                description = "unlimited lIVE classes \nHundreds of on-demand videos \nfree virtual training intro sessions. \ndiscounts on TRX products",
                cost = "$19.99 per month",
                startsAt = 1
            )
        }

        fun testList(count: Int): List<PlanModel> {
            return mutableListOf<PlanModel>().apply {
                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
        }
    }
}
