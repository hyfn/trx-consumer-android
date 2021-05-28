package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class FilterOptionsModel(
    val identifier: String = "",
    val value: String = "",
    var isSelected: Boolean = false
) : Parcelable {

    companion object {

        fun parse(jsonObject: JSONObject): List<FilterOptionsModel> {
            val options = mutableListOf<FilterOptionsModel>()
            val keys = jsonObject.keys()
            keys.forEach { key ->
                val model = FilterOptionsModel(identifier = key, value = jsonObject.optString(key))
                options.add(model)
            }
            return options
        }
    }

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterOptionsModel && other.value == value)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
