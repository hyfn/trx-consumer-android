package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterModel(
    val identifier: String = "",
    var title: String = "",
    val values: MutableList<FilterOptionsModel> = mutableListOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterModel && other.title == title)
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {
        fun test(): FilterModel {
            return FilterModel(
                "_identifier",
                "Filter",
                mutableListOf<FilterOptionsModel>().apply {
                    repeat(5) { index ->
                        add(FilterOptionsModel("option $index"))
                    }
                }
            )
        }

        fun testList(count: Int): List<FilterModel> {
            return mutableListOf<FilterModel>().apply {
                repeat(count) { index ->
                    val model = test().apply { title = "Filter $index" }
                    add(model)
                }
            }
        }
    }
}
