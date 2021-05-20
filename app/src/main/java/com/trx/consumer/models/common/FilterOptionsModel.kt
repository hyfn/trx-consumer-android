package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterOptionsModel(val value: String, var isSelected: Boolean = true) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterOptionsModel && other.value == value)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
