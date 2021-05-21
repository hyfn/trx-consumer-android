package com.trx.consumer.screens.discover.filters.filteroptions

import com.trx.consumer.models.common.FilterOptionsModel

interface FilterOptionsListener {
    fun doTapFilterOption(model: FilterOptionsModel)
}
