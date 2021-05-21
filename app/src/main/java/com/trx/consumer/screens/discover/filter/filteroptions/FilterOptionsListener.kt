package com.trx.consumer.screens.discover.filter.filteroptions

import com.trx.consumer.models.common.FilterOptionsModel

interface FilterOptionsListener {
    fun doTapFilterOption(model: FilterOptionsModel)
}
