package com.trx.consumer.screens.plans.list

import com.trx.consumer.models.common.PlansListModel

interface PlansListListener {
    fun doTapPlan(model: PlansListModel)
}
