package com.trx.consumer.models.common

import com.trx.consumer.extensions.map
import org.json.JSONObject

class PlansModel(
    var free: PlanModel = PlanModel(),
    var plans: List<PlanModel> = listOf()
) {

    companion object {

        fun parse(jsonObject: JSONObject): PlansModel {
            val free = jsonObject.getJSONArray("baseValues").map { base ->
                PlanModel.parseBaseValues(base)
            }.find { it.title.equals(UserModel.kPlanNamePay, true) } ?: PlanModel()

            val plans = jsonObject.getJSONObject("customValues").let { customValues ->
                customValues.keys().asSequence().toList().map {
                    PlanModel.parseCustomValues(
                        customValues.getJSONObject(it)
                    )
                }
            }
            return PlansModel(free, plans)
        }
    }
}
