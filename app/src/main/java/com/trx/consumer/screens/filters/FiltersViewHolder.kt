package com.trx.consumer.screens.filters

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.common.FilterValueModel

class FiltersViewHolder(view: View) : CommonViewHolder(view) {
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val viewFilter: CommonView = view.findViewById(R.id.viewFilter)

    fun setup(model: FilterValueModel, listener: FiltersListener) {
        lblTitle.text = model.name
        setBackGroundColor(model.isSelected)
        viewFilter.action {
            model.apply {
                isSelected = !isSelected
                listener.doTapFilterValue(this)
            }
        }
    }

    private fun setBackGroundColor(isSelected: Boolean) {
        lblTitle.apply {
            setTextColor(
                ContextCompat.getColor(context, if (isSelected) R.color.white else R.color.black)
            )
        }
        viewFilter.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    context, if (isSelected) R.color.black else R.color.greyLightExtra
                )
            )
        }
    }
}