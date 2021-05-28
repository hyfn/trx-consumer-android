package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.capitalized
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class FilterModel(
    val identifier: String = "",
    val title: String = "",
    val values: List<FilterOptionsModel> = listOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterModel && other.title == title)
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {

        fun filters(jsonObject: JSONObject): List<FilterModel> {
            val filters = mutableListOf<FilterModel>()
            val keys = jsonObject.keys()
            keys.forEach { key ->
                val value = jsonObject.optJSONObject(key)
                val model = FilterModel(
                    identifier = key,
                    title = key.capitalized(),
                    values = value?.let { FilterOptionsModel.parse(it) } ?: emptyList()
                )
                filters.add(model)
            }
            return filters
        }

        fun test(title: String): FilterModel {
            return FilterModel(
                title = "Filter",
                values = mutableListOf<FilterOptionsModel>().apply {
                    repeat(5) { index ->
                        add(FilterOptionsModel(value = "option $index"))
                    }
                }
            )
        }

        fun testList(count: Int): List<FilterModel> {
            return mutableListOf<FilterModel>().apply {
                repeat(count) { index ->
                    val model = test(title = "Filter $index")
                    add(model)
                }
            }
        }
    }
}
