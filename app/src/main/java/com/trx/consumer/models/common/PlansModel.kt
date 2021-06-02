package com.trx.consumer.models.common

import com.trx.consumer.extensions.map
import com.trx.consumer.managers.LogManager
import org.json.JSONObject
import java.lang.Exception

class PlansModel(
    var free: PlanModel = PlanModel(),
    var plans: List<PlanModel> = listOf()
) {

    companion object {

        fun parse(jsonObject: JSONObject): PlansModel =
            PlansModel(
                free = jsonObject.optJSONObject("free")?.let {
                    PlanModel.parse(it)
                } ?: PlanModel(),
                plans = try {
                    jsonObject.getJSONArray("plans").let { jsonArray ->
                        jsonArray.map { PlanModel.parse(it) }
                    }
                } catch (e: Exception) {
                    LogManager.log(e)
                    listOf()
                }
            )

        fun parseDev(jsonObject: JSONObject): PlansModel {
            val free = jsonObject.getJSONArray("baseValues").map { base ->
                PlanModel.parseDevBase(base)
            }.find { it.title.equals(UserModel.kPlanNamePay, true) } ?: PlanModel()

            val plans = jsonObject.getJSONObject("customValues").let { customValues ->
                customValues.keys().asSequence().toList().map {
                    PlanModel.parseDevCustom(
                        customValues.getJSONObject(it)
                    )
                }
            }
            return PlansModel(free, plans)
        }
    }
}
