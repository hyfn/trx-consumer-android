package com.trx.consumer.screens.discover.discoverfilter

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.px
import com.trx.consumer.models.common.FilterModel

class DiscoverFilterViewHolder(view: View) : CommonViewHolder(view) {

    private val lblFilter: CommonLabel = view.findViewById(R.id.lblFilter)
    private val viewMain: CommonView = view.findViewById(R.id.viewMain)

    fun setup(item: FilterModel, listener: DiscoverFilterListener) {
        lblFilter.text = item.title
        val isSelected = item.values.any { it.isSelected }
        lblFilter.textColor(if (isSelected) R.color.white else R.color.black)
        viewMain.bgColor(if (isSelected) R.color.black else R.color.white)
        if (isSelected) viewMain.border(R.color.grey, 1.px)
        viewMain.action { listener.doTapDiscoverFilter(item) }
    }
}
