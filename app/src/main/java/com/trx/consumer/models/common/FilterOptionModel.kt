package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterOptionModel(val name: String, var isSelected: Boolean = true) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterOptionModel && other.name == name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
