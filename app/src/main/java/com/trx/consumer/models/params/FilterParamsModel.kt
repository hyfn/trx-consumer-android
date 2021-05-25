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
                    if (selectedFilter.isNotEmpty()) {
                        /* initially user can select one option in each filter. logic below works
                          key and value as string when list has one element and list when element
                          more that one. Can change in future */
                        val optList = selectedFilter.map { it.identifier }
                        put(filter.title, if(optList.size == 1) optList.first() else optList)
                    }
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
