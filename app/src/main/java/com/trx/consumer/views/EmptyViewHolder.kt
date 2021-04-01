package com.trx.consumer.views

import android.view.View
import androidx.core.view.updateLayoutParams
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.px

class EmptyViewHolder(view: View) : CommonViewHolder(view) {

    fun setup(isColumn: Boolean = false) {
        if (isColumn) {
            itemView.updateLayoutParams {
                width = 15.px
            }
        }
    }
}
