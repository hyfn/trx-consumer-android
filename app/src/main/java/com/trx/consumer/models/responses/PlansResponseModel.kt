package com.trx.consumer.models.responses

import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.models.common.PlansModel
import com.trx.consumer.screens.plans.list.PlansViewState
import org.json.JSONObject

class PlansResponseModel(private var plan: PlansModel = PlansModel()) {

    fun plans(planText: String?): List<PlanModel> =
        planText?.let { text ->
            val list = plan.plans.toMutableList()
            list.indexOfFirst { it.title == text }.let { index ->
                if (index == -1) {
                    val current = plan.free.apply { primaryState = PlansViewState.CURRENT }
                    list.add(0, current)
                } else {
                    list.removeAt(index).let { planModel ->
                        list.clear()
                        planModel.primaryState = PlansViewState.CURRENT
                        list.add(0, planModel)
                    }
                }
            }
            list
        } ?: listOf()

    companion object {

        fun parse(json: String): PlansResponseModel {
            return PlansResponseModel(
                plan = JSONObject(json)
                    .getJSONObject("data")
                    .getJSONObject("customer").let {
                        PlansModel.parse(it)
                    }
            )
        }
    }
}
