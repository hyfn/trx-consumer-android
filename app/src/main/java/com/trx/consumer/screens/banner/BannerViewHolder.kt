package com.trx.consumer.screens.banner

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load

class BannerViewHolder(view: View) : CommonViewHolder(view) {

    private val imgBannerPhoto: CommonImageView = view.findViewById(R.id.imgBannerPhoto)

    fun setup(item: String, listener: BannerViewListener) {
        imgBannerPhoto.apply {
            load(item)
            action { listener.doTapBannerPrimaryPhotos(item) }
        }
    }
}
