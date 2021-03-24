package com.trx.consumer.extensions

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView

fun CommonImageView.load(
    url: String
) {
    Glide.with(context)
        .load(url)
        .fitCenter()
        .apply(
            RequestOptions().apply {
                placeholder(R.drawable.ic_img_placeholder)
                fallback(R.drawable.ic_img_placeholder)
            }
        )
        .into(this)
}

fun CommonImageView.setTint(@ColorRes colorId: Int) {
    val color = ContextCompat.getColor(context, colorId)
    val colorStateList = ColorStateList.valueOf(color)
    ImageViewCompat.setImageTintList(this, colorStateList)
}

fun CommonImageView.border(@ColorInt color: Int, width: Float) {
    strokeColor = ColorStateList.valueOf(color)
    strokeWidth = width * 2 // ShapeableImageView scales down stroke width by 1/2 before drawing
}
