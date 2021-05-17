package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoFilterModel (
    val id: Int,
    var title: String,
    val values : List<String> = listOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is VideoFilterModel && other.id == id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun test(count: Int): MutableList<VideoFilterModel> {
            val model = VideoFilterModel(25, "Test Filter", listOf("option1", "option2"))
            val list = mutableListOf<VideoFilterModel>()
            repeat(count) { index ->
                model.apply {
                    title = "$title $index"
                }
                list.add(model)
            }
            return list
        }
    }

}