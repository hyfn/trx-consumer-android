package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.FilterOptionsModel
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterParamsModel(
    var selectedModel: FilterModel = FilterModel(),
    var lstFilters: List<FilterModel> = listOf()
) : Parcelable {

    val params: HashMap<String, Any>
        get() {
            val params = hashMapOf<String, Any>()
            lstFilters.forEach { filter ->
                filter.values.find { it.isSelected }?.let { value ->
                    params[filter.identifier] = value.identifier
                }
            }
            return params
        }

    fun copyModel(): FilterParamsModel {
        val list = mutableListOf<FilterModel>()
        lstFilters.forEach { filter ->
            val options = mutableListOf<FilterOptionsModel>()
            filter.values.forEach { opt ->
                options.add(FilterOptionsModel(opt.identifier, opt.value, opt.isSelected))
            }
            list.add(FilterModel(filter.identifier, filter.title, options))
        }
        return FilterParamsModel(selectedModel = FilterModel(), lstFilters = list)
    }
}
