package com.trx.consumer.screens.filter

import com.trx.consumer.models.common.FilterModel

interface FiltersListener {
    fun doTapFilter(model: FilterModel)
}
