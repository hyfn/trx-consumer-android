package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.FilterModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterParamsModel(
    var selectedModel: FilterModel = FilterModel(),
    var list: List<FilterModel> = listOf()
) : Parcelable
