package com.trx.consumer.models.responses

import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.models.common.PlansModel
import com.trx.consumer.screens.plans.list.PlansViewState
import org.json.JSONObject

class PlansResponseModel(private var plan: PlansModel = PlansModel()) {

    fun plans(planText: String): List<PlanModel> {
        val list = plan.plans.toMutableList()
        list.indexOfFirst { it.title == planText }.let { index ->
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
        return list.filter { it.title.isNotEmpty() }
    }

    companion object {

        fun parse(json: String): PlansResponseModel =
            PlansResponseModel(
                plan = JSONObject(json)
                    .getJSONObject("data").let {
                        PlansModel.parse(it)
                    }
            )
    }
}
