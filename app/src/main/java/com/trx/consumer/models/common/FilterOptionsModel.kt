package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class FilterOptionsModel(var value: String = "", var isSelected: Boolean = false) : Parcelable {

    companion object {
        fun parse(jsonObject: JSONObject?): List<FilterOptionsModel> {
            return mutableListOf<FilterOptionsModel>().apply {
                jsonObject?.let { safeJson ->
                    val keys: Iterator<Any> = safeJson.keys()
                    while (keys.hasNext()) {
                        add(
                            FilterOptionsModel().apply {
                                val key = keys.next() as String
                                value = safeJson.optString(key)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterOptionsModel && other.value == value)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
