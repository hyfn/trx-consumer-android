package com.trx.consumer.screens.discover.filter

import com.trx.consumer.models.common.FilterModel

interface FiltersListener {
    fun doTapFilter(model: FilterModel)
}
