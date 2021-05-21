package com.trx.consumer.models.responses

import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.models.common.SubscriptionsModel
import com.trx.consumer.screens.plans.list.PlansViewState
import org.json.JSONObject

class PlansResponseModel(private var subscription: SubscriptionsModel = SubscriptionsModel()) {

    fun plans(subscriptionText: String?): List<PlanModel> {
        return subscriptionText?.let { text ->
            val list = subscription.plans.map { it.plan }.toMutableList()
            list.indexOfFirst { it.title == text }.let { index ->
                if (index != -1) {
                    list.removeAt(index).let { planModel ->
                        planModel.primaryState = PlansViewState.CURRENT
                        list.add(0, planModel)
                    }
                }
            }
            list
        } ?: subscription.plans.map { it.plan }
    }

    companion object {

        fun parse(json: String): PlansResponseModel {
            return PlansResponseModel(
                subscription = JSONObject(json)
                    .getJSONObject("data")
                    .getJSONObject("customer").let {
                        SubscriptionsModel.parse(it)
                    }
            )
        }
    }
}
