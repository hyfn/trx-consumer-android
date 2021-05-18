package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.FilterModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterParamsModel(
    var selectedFilter: Int = -1,
    var list: List<FilterModel> = listOf()
) : Parcelable
