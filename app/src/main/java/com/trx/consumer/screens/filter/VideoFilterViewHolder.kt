package com.trx.consumer.screens.filter

import android.view.View
import android.widget.Filter
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.VideoFilterModel

class VideoFilterViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val viewFilter: CommonView = view.findViewById(R.id.viewFilter)

    fun setup(item: VideoFilterModel, listener: FilterListener) {
        lblTitle.text  = item.title
        viewFilter.action { listener.doTapFilter(item) }
    }
}