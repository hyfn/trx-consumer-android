package com.trx.consumer.screens.discover.filters.filteroptions

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.FilterOptionsModel

class FilterOptionsViewHolder(view: View) : CommonViewHolder(view) {
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val viewFilter: CommonView = view.findViewById(R.id.viewFilter)

    fun setup(model: FilterOptionsModel, listener: FilterOptionsListener) {
        model.apply {
            lblTitle.text = value
            lblTitle.textColor(if (isSelected) R.color.white else R.color.black)
            viewFilter.bgColor(if (isSelected) R.color.black else R.color.greyLightExtra)
            viewFilter.action {
                isSelected = !isSelected
                listener.doTapFilterOption(this)
            }
        }
    }
}
