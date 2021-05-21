package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class FilterModel(
    var identifier: String = "",
    var title: String = "",
    var values: MutableList<FilterOptionsModel> = mutableListOf()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterModel && other.title == title)
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {
        fun filters(jsonObject: JSONObject?): ArrayList<FilterModel> {
            return ArrayList<FilterModel>().apply {
                jsonObject?.let { safeJson ->
                    val keys: Iterator<Any> = safeJson.keys()
                    while (keys.hasNext()) {
                        add(FilterModel().apply {
                            val key = keys.next() as String
                            title = key
                            val value = safeJson.optJSONObject(key)
                            values = FilterOptionsModel.parse(value)
                        })
                    }
                }
            }
        }

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

