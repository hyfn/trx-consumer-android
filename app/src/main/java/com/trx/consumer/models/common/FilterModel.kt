package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterModel(
    val id: Int = 0,
    val title: String = "",
    var values: List<FilterOptionModel> = listOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterModel && other.title == title)
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {
        fun test(count: Int): MutableList<FilterModel> {
            val list = mutableListOf<FilterModel>()
            repeat(count) { index ->
                list.add(
                    FilterModel(
                        index, "Test filter $index",
                        listOf(
                            FilterOptionModel("option1"),
                            FilterOptionModel("option2"),
                            FilterOptionModel("option3"),
                            FilterOptionModel("option4")
                        )
                    )
                )
            }
            return list
        }
    }
}
