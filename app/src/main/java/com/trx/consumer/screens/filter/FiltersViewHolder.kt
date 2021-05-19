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
        if (count == 1) {
            model.values.firstOrNull { it.isSelected }?.let { optionModel ->
                lblSelectedFilter.text = optionModel.name
            }
        } else if (count > 1 && count < model.values.size) {
            lblSelectedFilter.text = "Selected $count"
        } else {
            lblSelectedFilter.text = ""
        }
        viewFilter.action { listener.doTapFilter(model) }
    }
}
