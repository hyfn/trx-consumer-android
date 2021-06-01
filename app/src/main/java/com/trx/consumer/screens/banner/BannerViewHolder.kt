package com.trx.consumer.screens.banner

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.plans.list.PlansListener

class BannerViewHolder(view: View) : CommonViewHolder(view) {

    private val bannerView  : BannerView = view.findViewById(R.id.viewContent)

    fun setup(item: String, listener: BannerViewListener) {
        bannerView.loadView(item, listener)
    }
}