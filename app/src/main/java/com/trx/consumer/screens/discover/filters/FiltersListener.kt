package com.trx.consumer.screens.discover.filters

import com.trx.consumer.models.common.FilterModel

interface FiltersListener {
    fun doTapFilter(model: FilterModel)
}
