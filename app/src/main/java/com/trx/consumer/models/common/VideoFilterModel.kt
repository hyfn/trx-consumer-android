package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoFilterModel(
    val id: Int = 0,
    val title: String = "",
    val values: List<FilterOptionsModel> = listOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is VideoFilterModel && other.id == id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun test(count: Int): MutableList<VideoFilterModel> {
            val list = mutableListOf<VideoFilterModel>()
            repeat(count) { index ->
                list.add(
                    VideoFilterModel(
                        index, "Test filter $index",
                        listOf(FilterOptionsModel("option1"), FilterOptionsModel("option2"))
                    )
                )
            }
            return list
        }
    }
}
