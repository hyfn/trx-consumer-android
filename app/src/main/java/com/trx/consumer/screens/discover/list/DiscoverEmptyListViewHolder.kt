package com.trx.consumer.screens.discover.list

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder

class DiscoverEmptyListViewHolder(view: View) : CommonViewHolder(view) {

    private val lblEmpty: CommonLabel = view.findViewById(R.id.lblEmpty)

    fun setup(state: DiscoverEmptyListViewState) {
        lblEmpty.text = lblEmpty.context.getString(state.text)
    }
}
