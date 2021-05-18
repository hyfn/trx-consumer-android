package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterModel(
    val id: Int = 0,
    val title: String = "",
    val values: List<FilterOptionsModel> = listOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterModel && other.id == id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun test(count: Int): MutableList<FilterModel> {
            val list = mutableListOf<FilterModel>()
            repeat(count) { index ->
                list.add(
                    FilterModel(
                        index, "Test filter $index",
                        listOf(FilterOptionsModel("option1"), FilterOptionsModel("option2"))
                    )
                )
            }
            return list
        }
    }
}
