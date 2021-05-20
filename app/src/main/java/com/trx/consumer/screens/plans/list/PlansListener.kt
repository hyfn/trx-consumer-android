package com.trx.consumer.screens.plans.list

import com.trx.consumer.models.common.PlanModel

interface PlansListener {
    fun doTapPlan(model: PlanModel)
}
