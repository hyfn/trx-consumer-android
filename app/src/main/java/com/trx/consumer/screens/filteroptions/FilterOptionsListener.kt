package com.trx.consumer.screens.filteroptions

import com.trx.consumer.models.common.FilterOptionModel

interface FilterOptionsListener {
    fun doTapFilterOption(model: FilterOptionModel)
}
