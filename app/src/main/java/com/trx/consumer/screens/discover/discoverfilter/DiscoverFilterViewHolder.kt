package com.trx.consumer.screens.discover.discoverfilter

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.FilterModel

class DiscoverFilterViewHolder(view: View) : CommonViewHolder(view) {

    private val lblFilter: CommonLabel = view.findViewById(R.id.lblFilter)
    private val viewMain: CommonView = view.findViewById(R.id.viewMain)

    fun setup(item: FilterModel, listener: DiscoverFilterListener) {
        lblFilter.text = item.title
        viewMain.action { listener.doTapDiscoverFilter(item) }
    }
}
