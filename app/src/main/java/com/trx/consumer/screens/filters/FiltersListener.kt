package com.trx.consumer.screens.filters

import com.trx.consumer.common.FilterValueModel
import com.trx.consumer.models.common.VideoFilterModel

interface FiltersListener {
    fun doTapFilterValue(model : FilterValueModel)
}