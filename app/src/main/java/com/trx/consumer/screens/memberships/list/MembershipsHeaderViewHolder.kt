package com.trx.consumer.screens.memberships.list

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder

class MembershipsHeaderViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)

    fun setup(title: String) {
        lblTitle.text = title
    }

}