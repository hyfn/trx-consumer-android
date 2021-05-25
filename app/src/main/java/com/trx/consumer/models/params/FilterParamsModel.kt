package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.FilterOptionsModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterParamsModel(
    var selectedModel: FilterModel = FilterModel(),
    var lstFilters: List<FilterModel> = listOf()
) : Parcelable {

    val selectedFilterParams: HashMap<String, Any>?
        get() {
            val params = hashMapOf<String, Any>().apply {
                lstFilters.forEach { filter ->
                    val selectedFilter = filter.values.filter { it.isSelected }
                    if (selectedFilter.isNotEmpty())
                        put(filter.title, selectedFilter.map { it.identifier })
                }
            }
            return if (params.isNotEmpty()) params else null
        }

    fun copyModel(): FilterParamsModel {
        val list = mutableListOf<FilterModel>().apply {
            lstFilters.forEach {
                add(
                    FilterModel(
                        it.title,
                        mutableListOf<FilterOptionsModel>().apply {
                            it.values.forEach { opt ->
                                add(FilterOptionsModel(opt.identifier, opt.value, opt.isSelected))
                            }
                        }
                    )
                )
            }
        }
        return FilterParamsModel(selectedModel = FilterModel(), lstFilters = list)
    }
}
