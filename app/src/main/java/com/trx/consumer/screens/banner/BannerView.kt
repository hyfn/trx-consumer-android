package com.trx.consumer.screens.banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.trx.consumer.common.CommonView
import com.trx.consumer.databinding.RowBannerViewBinding
import com.trx.consumer.extensions.load

class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CommonView(context, attrs) {

    private val binding = RowBannerViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun loadView(model: String, listener: BannerViewListener) {
        binding.apply {
            imgBannerPhoto.load(model)
        }
    }
}
