package com.trx.consumer.common

import android.os.Parcelable
import com.trx.consumer.models.common.VideoFilterModel
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterValueModel (val name: String, var isSelected: Boolean = false) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other === this || (other is FilterValueModel && other.name == name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}