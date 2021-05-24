package com.trx.consumer.models.responses

import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.models.common.PlansModel
import com.trx.consumer.screens.plans.list.PlansViewState
import org.json.JSONObject

class PlansResponseModel(private var plan: PlansModel = PlansModel()) {

    fun plans(planText: String?): List<PlanModel> =
        planText?.let { text ->
            val list = plan.plans.map { it.plan }.toMutableList()
            list.indexOfFirst { it.title == text }.let { index ->
                if (index != -1) {
                    list.removeAt(index).let { planModel ->
                        planModel.primaryState = PlansViewState.CURRENT
                        list.add(0, planModel)
                    }
                }
            }
            list
        } ?: plan.plans.map { it.plan }

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
