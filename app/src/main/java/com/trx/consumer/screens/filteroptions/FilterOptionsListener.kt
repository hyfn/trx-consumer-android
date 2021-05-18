package com.trx.consumer.screens.filteroptions

import com.trx.consumer.models.common.FilterOptionsModel

interface FilterOptionsListener {
    fun doTapFilterValue(model : FilterOptionsModel)
}