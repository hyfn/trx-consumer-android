package com.trx.consumer.screens.filter

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.FilterModel

class FiltersViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSelectedFilter: CommonLabel = view.findViewById(R.id.lblSelectedFilter)
    private val viewFilter: CommonView = view.findViewById(R.id.viewFilter)

    fun setup(model: FilterModel, listener: FiltersListener) {
        lblTitle.text = model.title
        val count = model.values.count { it.isSelected }
        lblSelectedFilter.apply {
            text = if (count == 1) {
                model.values.find { it.isSelected }?.name
            } else if (count > 1 && count < model.values.size) {
                context.getString(R.string.filters_options_selected_filter_label, count)
            } else {
                context.getString(R.string.filters_options_selected_filter_default_label)
            }
            viewFilter.action { listener.doTapFilter(model) }
        }
    }
}
